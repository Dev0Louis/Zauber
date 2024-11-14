package dev.louis.zauber.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.louis.zauber.Zauber;
import dev.louis.zauber.entity.TelekinedBlockEntity;
import dev.louis.zauber.extension.EntityExtension;
import dev.louis.zauber.extension.PlayerEntityExtension;
import dev.louis.zauber.item.HeartOfTheDarknessItem;
import dev.louis.zauber.item.ZauberItems;
import dev.louis.zauber.networking.play.s2c.TelekinesisStatePayload;
import dev.louis.zauber.tag.ZauberItemTags;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.Predicate;

@SuppressWarnings("UnreachableCode")
@Mixin(value = PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerEntityExtension {

    @Shadow public abstract void playSound(SoundEvent sound, float volume, float pitch);

    @Unique
    @Nullable
    private Entity telekined;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(
            method = "applyDamage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setHealth(F)V")
    )
    public void stopTelekinesisOnDamage(DamageSource source, float amount, CallbackInfo ci) {
        this.zauber$getTelekineser().ifPresent(PlayerEntityExtension::zauber$stopTelekinesis);
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
        if (this.telekined != null && !this.getWorld().isClient()) {
            this.telekined.zauber$removeTelekinesisFrom((PlayerEntity) (Object) this);
            //TODO: Remove special caseing
            if (this.telekined instanceof TelekinedBlockEntity telekinedBlockEntity) {
                telekinedBlockEntity.loseOwner();
            }
        }

        this.telekined = newTelekinesisEntity;
        if (newTelekinesisEntity != null) {
            newTelekinesisEntity.zauber$setTelekineser((PlayerEntity) (Object) this);
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
        /*if (!(this.getStackInHand(this.getActiveHand()).isOf(ZauberItems.STAFF))) {
            var oldTelekined = telekined;
            telekined = null;
            if (oldTelekined != null && (!oldTelekined.isAlive())) {
                oldTelekined.zauber$removeTelekinesisFrom((PlayerEntity) (Object) this);
                if (!this.getWorld().isClient()) {
                    this.syncTelekinesisState();
                }
            }
        }*/
        var telekined = this.telekined;
        if (telekined != null) {
            if (!this.getWorld().isClient()) {
                Vec3d target = switch (telekined) {
                    case TelekinedBlockEntity ignored -> this.getCameraPosVec(0).add(this.getRotationVector().multiply(5)).add(0, -0.5, 0);
                    default ->                           this.getCameraPosVec(0).add(this.getRotationVector().multiply(5));
                };
                telekined.setVelocity(telekined.getVelocity().multiply(0.75));
                var vel = target.subtract(telekined.getPos()).multiply(0.1);
                telekined.velocityModified = true;
                if (telekined instanceof PlayerEntity) {
                    telekined.setVelocity(vel);
                } else {
                    telekined.addVelocity(vel);
                }
                telekined.move(MovementType.SELF, telekined.getVelocity());
            } else {
                Vec3d vec3d = telekined.getVelocity();
                double dX = telekined.getX() + vec3d.x;
                double dY = telekined.getY() + vec3d.y;
                double dZ = telekined.getZ() + vec3d.z;
                telekined.setPosition(dX, dY, dZ);
            }
        }
    }

    /*@ModifyReturnValue(
            method = "shouldCancelInteraction",
            at = @At("RETURN")
    )
    public boolean cancelInteractionWithStaff(boolean original) {
        //TODO: Figure out why this isn't fixing the horse interactions?
        return original || this.getMainHandStack().isOf(ZauberItems.STAFF);
    }*/

    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        TelekinesisStatePayload payload = new TelekinesisStatePayload((PlayerEntity) (Object) this, telekined);
        ServerPlayNetworking.send(player, payload);
    }

    @Unique
    private void syncTelekinesisState() {
        var serverWorld = (ServerWorld) this.getWorld();
        TelekinesisStatePayload payload = new TelekinesisStatePayload((PlayerEntity) (Object) this, telekined);
        for (int j = 0; j < serverWorld.getPlayers().size(); j++) {
            ServerPlayerEntity player = serverWorld.getPlayers().get(j);
            serverWorld.sendToPlayerIfNearby(player, false, this.getX(), this.getY(), this.getZ(), ServerPlayNetworking.createS2CPacket(payload));
            System.out.println("Send: " + payload);
        }
    }

    @Override
    public void zauber$throwTelekined() {
        if(telekined != null) {
            if (this.telekined instanceof TelekinedBlockEntity telekinedBlockEntity) {
                telekinedBlockEntity.throwBlock();
            } else {
                this.telekined.addVelocity(this.telekined.getPos().subtract(this.getPos()).multiply(0.2));
            }

            this.telekined.addVelocity(this.getVelocity());
            this.telekined.zauber$removeTelekinesisFrom((PlayerEntity) (Object) this);
            this.telekined = null;
        }

        if (!this.getWorld().isClient()) {
            syncTelekinesisState();
        }
    }

    @Override
    public void zauber$stopTelekinesis() {
        if(telekined != null) {
            if (this.telekined instanceof TelekinedBlockEntity telekinedBlockEntity) {
                telekinedBlockEntity.loseOwner();
            }

            this.telekined.zauber$removeTelekinesisFrom((PlayerEntity) (Object) this);
            this.telekined = null;
        }

        if (!this.getWorld().isClient()) {
            syncTelekinesisState();
        }
    }

    @Override
    public Optional<Entity> zauber$getTelekined() {
        return Optional.ofNullable(telekined);
    }
}
