package dev.louis.zauber.mixin;

import dev.louis.zauber.Zauber;
import dev.louis.zauber.extension.EntityExtension;
import dev.louis.zauber.extension.PlayerEntityExtension;
import dev.louis.zauber.entity.BlockTelekinesisEntity;
import dev.louis.zauber.item.HeartOfTheDarknessItem;
import dev.louis.zauber.item.ZauberItems;
import dev.louis.zauber.networking.TelekinesisPayload;
import dev.louis.zauber.tag.ZauberItemTags;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.function.Predicate;

@SuppressWarnings("UnreachableCode")
@Mixin(value = PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerEntityExtension {

    @Shadow public abstract void playSound(SoundEvent sound, float volume, float pitch);

    private static final int TARGETING_DISTANCE = 20;
    @Nullable
    private Entity telekinesisEntity;


    @Nullable
    private LivingEntity staffTargetedEntity;
    @Nullable
    private CachedBlockPosition staffTargetedBlock;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    public void makeHeartDisappearInLight(CallbackInfo ci) {
        if (!this.getWorld().isClient) {
            for (Hand hand : Hand.values()) {
                var stack = this.getStackInHand(hand);
                if (stack.isIn(ZauberItemTags.DESTROYED_BY_LIGHT) && this.getWorld().getLightLevel(this.getBlockPos()) > HeartOfTheDarknessItem.MAX_BRIGHTNESS) {
                    HeartOfTheDarknessItem.onDisappeared((ServerWorld) this.getWorld(), this.getEyePos());
                    stack.decrement(1);
                }
            }
            if (age % 20 == 0) {
                if (Zauber.isInTrappingBed((PlayerEntity) (Object) this)) {
                    this.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 40));
                }
            }
        }
    }

    @Override
    public void zauber$startTelekinesisOn(@Nullable Entity newTelekinesisEntity) {
        if (this.telekinesisEntity != null && !this.getWorld().isClient()) {
            ((EntityExtension) telekinesisEntity).removeTelinesisFrom((PlayerEntity) (Object) this);
            //TODO: Remove special caseing
            if (this.telekinesisEntity instanceof BlockTelekinesisEntity blockTelekinesisEntity) {
                blockTelekinesisEntity.loseOwner();
            }
        }

        this.telekinesisEntity = newTelekinesisEntity;
        if (newTelekinesisEntity != null) {
            ((EntityExtension) newTelekinesisEntity).setTelekineser((PlayerEntity) (Object) this);
        }

        if (!this.getWorld().isClient()) {
            syncTelekinesisState();
        }
    }

    @Inject(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;tick()V")
    )
    public void staffStuff(CallbackInfo ci) {
        staffTargetedEntity = null;
        staffTargetedBlock = null;
        if (telekinesisEntity != null && (!telekinesisEntity.isAlive())) {
            ((EntityExtension) telekinesisEntity).removeTelinesisFrom((PlayerEntity) (Object) this);
            telekinesisEntity = null;
            if (!this.getWorld().isClient()) {
                this.syncTelekinesisState();
            }
        }

        if (this.getStackInHand(this.getActiveHand()).isOf(ZauberItems.STAFF)) {
            getTargetedEntity(TARGETING_DISTANCE)
                    .filter(LivingEntity.class::isInstance)
                    .map(LivingEntity.class::cast)
                    .ifPresent(entity -> staffTargetedEntity = entity);

            var rayCast = this.raycast(TARGETING_DISTANCE, 0, false);
            if (rayCast.getType() == HitResult.Type.BLOCK) {
                var world = this.getWorld();
                var pos = ((BlockHitResult) rayCast).getBlockPos();
                staffTargetedBlock = new CachedBlockPosition(world, pos, false);
            }

            //System.out.println(this.getWorld().isClient() + " " + newTelekinesisEntity);

            if (telekinesisEntity != null) {
                if (!this.getWorld().isClient()) {
                    var target = this.getEyePos().add(this.getRotationVector().normalize().multiply(6).add(0, -.5, 0));
                    telekinesisEntity.setVelocity(telekinesisEntity.getVelocity().multiply(0.75));
                    var vel = target.subtract(telekinesisEntity.getPos()).multiply(0.1);
                    telekinesisEntity.velocityModified = true;
                    telekinesisEntity.addVelocity(vel);
                    telekinesisEntity.move(MovementType.SELF, telekinesisEntity.getVelocity());
                } else {
                    Vec3d vec3d = telekinesisEntity.getVelocity();
                    double dX = telekinesisEntity.getX() + vec3d.x;
                    double dY = telekinesisEntity.getY() + vec3d.y;
                    double dZ = telekinesisEntity.getZ() + vec3d.z;
                    telekinesisEntity.setPosition(dX, dY, dZ);
                }
            }
        }
    }

    private Optional<Entity> getTargetedEntity(int maxDistance) {
        Vec3d eyePos = this.getEyePos();
        Vec3d rotation = this.getRotationVec(1.0F).multiply(maxDistance);
        Vec3d start = eyePos.add(rotation);
        Box box = this.getBoundingBox().stretch(rotation).expand(1.0);
        int maxDistanceSquared = maxDistance * maxDistance;
        Predicate<Entity> predicate = entityx -> !entityx.isSpectator() && entityx.canHit() && entityx.getVehicle() == null;
        EntityHitResult entityHitResult = ProjectileUtil.raycast(this, eyePos, start, box, predicate, maxDistanceSquared);
        if (entityHitResult == null) {
            return Optional.empty();
        } else {
            return eyePos.squaredDistanceTo(entityHitResult.getPos()) > (double) maxDistanceSquared ? Optional.empty() : Optional.of(entityHitResult.getEntity());
        }
    }

    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        TelekinesisPayload payload = new TelekinesisPayload((PlayerEntity) (Object) this, telekinesisEntity);
        ServerPlayNetworking.send(player, payload);
    }

    @Unique
    private void syncTelekinesisState() {
        var serverWorld = (ServerWorld) this.getWorld();
        TelekinesisPayload payload = new TelekinesisPayload((PlayerEntity) (Object) this, telekinesisEntity);
        for (int j = 0; j < serverWorld.getPlayers().size(); j++) {
            ServerPlayerEntity player = serverWorld.getPlayers().get(j);
            serverWorld.sendToPlayerIfNearby(player, false, this.getX(), this.getY(), this.getZ(), ServerPlayNetworking.createS2CPacket(payload));
                System.out.println("Send: " + payload);
        }
    }

    @Override
    public Optional<LivingEntity> getStaffTargetedEntity() {
        return Optional.ofNullable(staffTargetedEntity);
    }

    @Override
    public Optional<CachedBlockPosition> getStaffTargetedBlock() {
        return Optional.ofNullable(staffTargetedBlock);
    }

    @Override
    public void zauber$throwTelekinesis() {
        if(telekinesisEntity != null) {
            if (telekinesisEntity instanceof BlockTelekinesisEntity blockTelekinesisEntity) {
                blockTelekinesisEntity.throwBlock();
            } else {
                telekinesisEntity.setVelocity(telekinesisEntity.getPos().subtract(this.getPos()).multiply(0.2));
            }

            ((EntityExtension) telekinesisEntity).removeTelinesisFrom((PlayerEntity) (Object) this);
            telekinesisEntity = null;
        }

        if (!this.getWorld().isClient()) {
            syncTelekinesisState();
        }
    }

    @Override
    public void zauber$stopTelekinesis() {
        if(telekinesisEntity != null) {
            if (telekinesisEntity instanceof BlockTelekinesisEntity blockTelekinesisEntity) {
                blockTelekinesisEntity.loseOwner();
            }

            ((EntityExtension) telekinesisEntity).removeTelinesisFrom((PlayerEntity) (Object) this);
            telekinesisEntity = null;
        }

        if (!this.getWorld().isClient()) {
            syncTelekinesisState();
        }
    }

    @Override
    public Optional<Entity> zauber$getTelekinesisAffected() {
        return Optional.ofNullable(telekinesisEntity);
    }
}
