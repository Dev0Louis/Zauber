package dev.louis.zauber.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.louis.zauber.Zauber;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @ModifyReturnValue(
            method = "isPushable", at = @At("RETURN"))
    public boolean dashingPlayersAreNotPushable(boolean original) {
        return original || ((Object)this instanceof PlayerEntity player && player.getSpellManager().isSpellTypeActive(Zauber.Spells.DASH));
    }
}
