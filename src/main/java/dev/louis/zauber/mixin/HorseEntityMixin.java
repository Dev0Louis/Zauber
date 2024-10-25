package dev.louis.zauber.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.louis.zauber.item.ZauberItems;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Debug(export = true)
@Mixin(HorseEntity.class)
public class HorseEntityMixin {
    @ModifyVariable(
            method = "interactMob",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/HorseEntity;hasPassengers()Z"),
            ordinal = 0
    )
    public boolean fixHorseStaffInteraction(
            boolean original,
            @Local(argsOnly = true) PlayerEntity player,
            @Local(argsOnly = true) Hand hand
    ) {
        return original || player.getStackInHand(hand).isOf(ZauberItems.STAFF);
    }
}
