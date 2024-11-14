package dev.louis.zauber.client.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.zauber.client.ZauberClient;
import dev.louis.zauber.client.extension.MinecraftClientExtension;
import dev.louis.zauber.client.glisco.StencilFramebuffer;
import dev.louis.zauber.config.ConfigManager;
import dev.louis.zauber.networking.play.c2s.StartTelekinesisPayload;
import dev.louis.zauber.networking.play.c2s.StopTelekinesisPayload;
import dev.louis.zauber.networking.play.c2s.ThrowTelekinedPayload;
import dev.louis.zauber.spell.type.PlayerSpellFactory;
import dev.louis.zauber.spell.type.SpellType;
import dev.louis.zauber.telekinesis.TelekinesisTarget;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.TickDurationMonitor;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin implements MinecraftClientExtension {


    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Shadow @Final public GameOptions options;
    @Shadow @Nullable public HitResult crosshairTarget;

    @Shadow @Final public GameRenderer gameRenderer;
    @Unique
    private TelekinesisTarget telekinesisTarget;

    @Shadow public abstract @Nullable Entity getCameraEntity();

    @Shadow public abstract Window getWindow();

    @Shadow @Nullable public ClientWorld world;
    @Shadow @Nullable public Entity cameraEntity;

    @Shadow protected abstract Profiler startMonitor(boolean active, @Nullable TickDurationMonitor monitor);

    @Unique
    int spellCooldown = 0;

    @Unique
    private static final int NEEDED_TELEKINESIS_TICKS = 15;

    @Unique
    int telekinesisAttemptTicks;
    @Unique
    int telekinesisDropCooldown;

    @Inject(method = "handleInputEvents", at = @At("HEAD"))
    private void handelInputEventsForNebula(CallbackInfo ci) {
        if (spellCooldown > 0) {
            spellCooldown--;
            return;
        }

        for (SpellType<?> spellType : SpellType.REGISTRY) {
            var optionalKey = ZauberClient.getSpellKeybindManager().getKey(spellType);
            if (optionalKey.isPresent()) {
                var key = optionalKey.get();
                if (key.isPressed()) {
                    Spell<LivingEntity> spell = switch (spellType.factory()) {
                        case PlayerSpellFactory<?> simpleSpellFactory -> simpleSpellFactory.create();
                    };
                    SpellSource.of((LivingEntity) player).castSpell(spell);
                    this.resetSpellCooldown();
                    return;
                }
            }
        }
    }

    public void resetSpellCooldown() {
        spellCooldown = ConfigManager.getServerConfig().spellCooldown();
    }


    @Inject(
            method = "onResolutionChanged",
            at = @At("TAIL")
    )
    public void resizeStencilBuffer(CallbackInfo ci) {
            StencilFramebuffer.stencilFrameBuffer = new StencilFramebuffer(MinecraftClient.getInstance().getWindow().getFramebufferWidth(), MinecraftClient.getInstance().getWindow().getFramebufferHeight());
    }


    @WrapOperation(
            method = "handleInputEvents",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;doAttack()Z")
    )
    public boolean throwTelekinesisInsteadOfAttack (MinecraftClient instance, Operation<Boolean> original) {
        if (telekinesisDropCooldown <= 0) {
            var thrown = this.player.zauber$getTelekined().map(entity -> {
                ClientPlayNetworking.send(new ThrowTelekinedPayload());
                return true;
            }).orElse(false);

            if (thrown) return true;
        }

        return original.call(instance);
    }

    @WrapMethod(
            method = "doItemUse"
    )
    public void cancelUseWhenTryingToStartTelekinesis(Operation<Void> original) {
        if (
                !(this.player != null &&
                        this.player.isSneaking() &&
                        this.player.getMainHandStack().isEmpty() &&
                        !this.player.zauber$isUsingTelekinesis() &&
                        this.options.useKey.isPressed() &&
                        !(this.crosshairTarget.getType() == HitResult.Type.MISS))
        ) {
            original.call();
        }
    }

    @Inject(
            method = "tick",
            at = @At("RETURN")
    )
    public void resetTelekinesisAttemptTicks(CallbackInfo ci) {
        if (this.player == null) return;
        this.telekinesisDropCooldown--;
        this.telekinesisTarget = findTelekinesisTarget(16, 16, 1);


        if (this.player.zauber$isUsingTelekinesis()) {
            if (telekinesisDropCooldown <= 0) {
                if (this.player.isSneaking() && this.options.useKey.isPressed()) {
                    ClientPlayNetworking.send(new StopTelekinesisPayload(this.player.isSneaking()));
                }
            }
        } else {
            if (
                    this.player.isSneaking() &&
                            this.player.getMainHandStack().isEmpty() &&
                            this.options.useKey.isPressed() &&
                            (telekinesisTarget != null)
            ) {
                if (this.telekinesisAttemptTicks == NEEDED_TELEKINESIS_TICKS) {
                    ClientPlayNetworking.send(new StartTelekinesisPayload(this.telekinesisTarget));
                    this.telekinesisAttemptTicks = 0;
                    this.telekinesisDropCooldown = 20;
                    //TODO: Add particles around trageted.
                    return;
                }
                this.telekinesisAttemptTicks++;
            } else {
                this.telekinesisAttemptTicks = 0;
            }
        }
    }

    public float zauber$getTelekinesisStartPogress() {
        return telekinesisAttemptTicks / (float) NEEDED_TELEKINESIS_TICKS;
    }


    public final TelekinesisTarget findTelekinesisTarget(double blockInteractionRange, double entityInteractionRange, float tickDelta) {
        Entity camera = this.cameraEntity;
        double d = Math.max(blockInteractionRange, entityInteractionRange);
        double e = MathHelper.square(d);
        Vec3d vec3d = camera.getCameraPosVec(tickDelta);
        HitResult hitResult = raycastBlock(d, tickDelta);
        double f = hitResult.getPos().squaredDistanceTo(vec3d);
        if (hitResult.getType() != HitResult.Type.MISS) {
            e = f;
            d = Math.sqrt(f);
        }

        Vec3d vec3d2 = camera.getRotationVec(tickDelta);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
        Box box = camera.getBoundingBox().stretch(vec3d2.multiply(d)).expand(1.0, 1.0, 1.0);
        EntityHitResult entityHitResult = ProjectileUtil.raycast(camera, vec3d, vec3d3, box, entity -> !entity.isSpectator() && entity.canHit(), e);
        var endHitResult = entityHitResult != null && entityHitResult.getPos().squaredDistanceTo(vec3d) < f
                ? ensureTargetInRange(entityHitResult, vec3d, entityInteractionRange)
                : ensureTargetInRange(hitResult, vec3d, blockInteractionRange);
        return switch (hitResult.getType()) {
            case BLOCK -> new TelekinesisTarget.BlockTarget(((BlockHitResult) endHitResult).getBlockPos());
            case ENTITY -> new TelekinesisTarget.EntityTarget(((EntityHitResult) endHitResult).getEntity());
            default -> throw new IllegalStateException();
        };
    }

    public HitResult raycastBlock(double maxDistance, float tickDelta) {
        Vec3d start = this.cameraEntity.getCameraPosVec(tickDelta);
        Vec3d rot = this.cameraEntity.getRotationVec(tickDelta);
        Vec3d end = start.add(rot.x * maxDistance, rot.y * maxDistance, rot.z * maxDistance);
        return this.world
                .raycast(
                        new RaycastContext(
                                start, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this.cameraEntity
                        )
                );
    }

    private static HitResult ensureTargetInRange(HitResult hitResult, Vec3d cameraPos, double interactionRange) {
        Vec3d vec3d = hitResult.getPos();
        if (!vec3d.isInRange(cameraPos, interactionRange)) {
            Vec3d vec3d2 = hitResult.getPos();
            Direction direction = Direction.getFacing(vec3d2.x - cameraPos.x, vec3d2.y - cameraPos.y, vec3d2.z - cameraPos.z);
            return BlockHitResult.createMissed(vec3d2, direction, BlockPos.ofFloored(vec3d2));
        } else {
            return hitResult;
        }
    }

}
