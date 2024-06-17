package dev.louis.zauber.entity;

import dev.louis.zauber.Zauber;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class ManaArrowEntity extends ArrowEntity {
    public static final EntityType<ManaArrowEntity> TYPE = EntityType.Builder.<ManaArrowEntity>create(ManaArrowEntity::new, SpawnGroup.MISC).setDimensions(0.5F, 0.5F).maxTrackingRange(4).trackingTickInterval(20).build("mana_arrow");

    public ManaArrowEntity(EntityType<? extends ArrowEntity> entityType, World world) {
        super(entityType, world);
    }

    public ManaArrowEntity(World world, LivingEntity owner, ItemStack stack) {
        super(TYPE, world);
        this.setOwner(owner);
        this.setPos(owner.getX(), owner.getEyeY() - 0.1F, owner.getZ());
        this.stack = stack;
        this.setNoClip(true);
    }

    @Override
    public void tick() {
        //System.out.println((this.getWorld().isClient ? "client" : "server") + " noClip " + noClip);
        //System.out.println((this.getWorld().isClient ? "client" : "server") + " noGravity " + hasNoGravity());
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        this.getWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), 1, World.ExplosionSourceType.MOB);
        super.onEntityHit(entityHitResult);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
    }

    public EntityType<?> getPolymerEntityType(ServerPlayerEntity player) {
        if(Zauber.isClientModded(player)) return TYPE;
        return EntityType.ARROW;
    }
}
