package dev.louis.zauber.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.louis.zauber.item.ManaBowItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BowItem.class)
public class BowItemMixin {
    @SuppressWarnings("UnreachableCode")
    @WrapOperation(
            method = "onStoppedUsing",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ArrowItem;createArrow(Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/entity/projectile/PersistentProjectileEntity;")
    )
    public PersistentProjectileEntity allowManaBowToCreateOwnArrow(ArrowItem item, World world, ItemStack stack, LivingEntity shooter, Operation<PersistentProjectileEntity> original) {
        if (((Object) this) instanceof ManaBowItem manaBowItem) {
            return manaBowItem.createArrow(item, world, stack, shooter);
        }
        return original.call(item, world, stack, shooter);
    }
}
