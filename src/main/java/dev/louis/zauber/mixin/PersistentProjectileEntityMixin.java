package dev.louis.zauber.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import dev.louis.zauber.entity.ManaArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin {

    //We modify the original assignment of isNoClip here as we can't wrap the ILOAD at isNoClip specific place (Mixin skill issue ;))
    @ModifyExpressionValue(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;isNoClip()Z")
    )
    public boolean makeManaArrowPassThroughBlocks(boolean isNoClip, @Share("originalIsNoClip") LocalBooleanRef originalIsNoClip) {
        originalIsNoClip.set(isNoClip);
        return isNoClip || ((Object) this) instanceof ManaArrowEntity;
    }

    //Here we again set the correct value to the correct Value for all following methods.
    @ModifyVariable(
            method = "tick",
            at = @At(value = "FIELD", target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;shake:I")
    )
    public boolean resetNoClipToCorrectValue(boolean isNoClip, @Share("originalIsNoClip") LocalBooleanRef originalIsNoClip) {
        return originalIsNoClip.get();
    }

    @SuppressWarnings("UnreachableCode")
    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;raycast(Lnet/minecraft/world/RaycastContext;)Lnet/minecraft/util/hit/BlockHitResult;"),
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;getPos()Lnet/minecraft/util/math/Vec3d;", ordinal = 1))
    )
    public BlockHitResult dontRaycastIfManaArrowToMakeEntityHittingWorkThroughWalls(World instance, RaycastContext raycastContext, Operation<BlockHitResult> original, @Local(ordinal = 1) Vec3d start, @Local(ordinal = 2) Vec3d end) {
        if (((Object) this) instanceof ManaArrowEntity) {
            Vec3d direction = start.subtract(end);
            return BlockHitResult.createMissed(end, Direction.getFacing(direction.x, direction.y, direction.z), BlockPos.ofFloored(end));
        }
        return original.call(instance, raycastContext);
    }
}
