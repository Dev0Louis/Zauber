package dev.louis.zauber.mixin;

import dev.louis.nebula.mana.NebulaManaManager;
import dev.louis.zauber.entity.TotemOfManaEntity;
import dev.louis.zauber.extension.EntityWithFollowingEntities;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = NebulaManaManager.class, remap = false)
public class NebulaManaManagerMixin {
    @Shadow protected LivingEntity entity;

    @ModifyConstant (
            method = "regenMana",
            constant = @Constant(floatValue = 0.016666668F)
    )
    public float modifyRegenRate(float regenRate) {
        if (this.entity instanceof EntityWithFollowingEntities entityWithFollowingEntities) {
            var hasMana = entityWithFollowingEntities.zauber$getFollowingEntities().stream().anyMatch(entity1 -> entity1 instanceof TotemOfManaEntity);
            if (hasMana) {
                return regenRate + 0.05f;
            }
        }
        return regenRate;
    }
}
