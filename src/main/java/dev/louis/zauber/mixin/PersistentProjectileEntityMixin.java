package dev.louis.zauber.mixin;

import dev.louis.zauber.accessor.ArrowEntityAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin extends ProjectileEntity implements ArrowEntityAccessor {
    public PersistentProjectileEntityMixin(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    public boolean damageOwner = true;

    @Override
    public boolean zauber$shouldDamageOwner() {
        return damageOwner;
    }

    @Override
    public void zauber$shouldDamageOwner(boolean damageOwner) {
        this.damageOwner = damageOwner;
    }

    @Inject(method = "onEntityHit", at = @At("HEAD"), cancellable = true)
    public void noOwnerDamageMixin(EntityHitResult entityHitResult, CallbackInfo ci) {
        Entity target = entityHitResult.getEntity();
        if(!damageOwner) {
            if(this.getOwner() == null)return;
            if(target.equals(this.getOwner())) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "onBlockHit", at = @At("HEAD"))
    public void despawnOnHit(CallbackInfo ci) {
        if(damageOwner)return;
        this.remove(RemovalReason.DISCARDED);

    }
}
