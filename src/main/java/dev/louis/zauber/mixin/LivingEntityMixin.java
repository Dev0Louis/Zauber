package dev.louis.zauber.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.louis.zauber.accessor.DashingLivingEntity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements DashingLivingEntity {
    @Unique
    private boolean dashing = false;

    @ModifyReturnValue(
            method = "isPushable", at = @At("RETURN"))
    public boolean dashingPlayersAreNotPushable(boolean original) {
        return original || dashing;
    }

    public void zauber$setDashing(boolean dashing) {
        this.dashing = dashing;
    }
}
