package dev.louis.zauber.entity;

import dev.louis.zauber.Zauber;
import dev.louis.zauber.item.ZauberItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ThrownHeartOfTheIceEntity extends ThrownItemEntity {
    public static final EntityType<ThrownHeartOfTheIceEntity> TYPE = EntityType.Builder.<ThrownHeartOfTheIceEntity>create(ThrownHeartOfTheIceEntity::new, SpawnGroup.MISC).dimensions(0.25F, 0.25F).maxTrackingRange(4).trackingTickInterval(10).build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Zauber.MOD_ID, "thrown_heart_of_the_ice")));


    public ThrownHeartOfTheIceEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public ThrownHeartOfTheIceEntity(double x, double y, double z, World world, ItemStack stack) {
        super(TYPE, x, y, z, world, stack);
    }

    public ThrownHeartOfTheIceEntity(LivingEntity owner, World world, ItemStack stack) {
        super(TYPE, owner, world, stack);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        if (!this.getWorld().isClient()) {
            BlockPos spawnPos = blockHitResult.getBlockPos().up();

            IcePeakEntity.TYPE.spawn((ServerWorld) this.getWorld(), spawnPos, SpawnReason.MOB_SUMMONED);
            this.discard();
        }
        super.onBlockHit(blockHitResult);
    }

    @Override
    protected Item getDefaultItem() {
        return ZauberItems.HEART_OF_THE_ICE;
    }
}
