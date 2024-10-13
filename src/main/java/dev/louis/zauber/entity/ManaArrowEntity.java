package dev.louis.zauber.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class ManaArrowEntity extends PersistentProjectileEntity {
    private static final ItemStack DEFAULT_STACK = new ItemStack(Items.ARROW);
    public static final EntityType<ManaArrowEntity> TYPE = EntityType.Builder.<ManaArrowEntity>create(ManaArrowEntity::new, SpawnGroup.MISC).dimensions(0.5F, 0.5F).maxTrackingRange(4).trackingTickInterval(20).build("mana_arrow");

    public ManaArrowEntity(EntityType<? extends ManaArrowEntity> entityType, World world) {
        super(entityType, world);
    }

    public ManaArrowEntity(World world, LivingEntity owner, ItemStack stack) {
        super(TYPE, owner, world, stack, null);
    }


    public ManaArrowEntity(World world, double x, double y, double z, ItemStack stack) {
        super(TYPE, x, y, z, world, stack, null);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {

    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return DEFAULT_STACK;
    }


    @Override
    protected void onCollision(HitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            this.onEntityHit((EntityHitResult) hitResult);
            this.getWorld().emitGameEvent(GameEvent.PROJECTILE_LAND, hitResult.getPos(), GameEvent.Emitter.of(this, null));
        }
    }
}
