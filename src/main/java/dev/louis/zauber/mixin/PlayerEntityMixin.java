package dev.louis.zauber.mixin;

import dev.louis.zauber.Zauber;
import dev.louis.zauber.item.HeartOfTheDarknessItem;
import dev.louis.zauber.tag.ZauberItemTags;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("UnreachableCode")
@Mixin(value = PlayerEntity.class, priority = 900)
public abstract class PlayerEntityMixin extends LivingEntity {
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
}
