package dev.louis.zauber.entity;

import dev.louis.zauber.Zauber;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

import java.util.List;

public class HailStoneEntity extends ThrownItemEntity implements PolymerEntity {
    public static final EntityType<HailStoneEntity> TYPE = FabricEntityTypeBuilder
            .create(SpawnGroup.MISC, HailStoneEntity::new)
            .build();
    private static final int BASE_DAMAGE = 4;
    private boolean bounce = this.random.nextBoolean();
    public boolean castedWithIceTotem;

    public HailStoneEntity(EntityType<? extends ThrownItemEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.ICE;
    }

    @Override
    public ItemStack getStack() {
        return Items.ICE.getDefaultStack();
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        if (bounce && hitResult.getType() == HitResult.Type.BLOCK) {
            bounce = false;
            //TODO: Make good bouncing https://stackoverflow.com/questions/573084/how-to-calculate-bounce-angle
            double energyKept = 0.2;
            this.setVelocity(this.getVelocity().multiply(energyKept, -energyKept, energyKept));
        } else {
            makeBreakEffect();
            this.discard();
        }
        if (hitResult.getType() == HitResult.Type.ENTITY && ((EntityHitResult) hitResult).getEntity() instanceof LivingEntity livingEntity) {
            damageEntity(livingEntity);
        }
        this.getWorld().getOtherEntities(null, this.getBoundingBox()).stream().filter(LivingEntity.class::isInstance).map(LivingEntity.class::cast).forEach(this::damageEntity);
    }

    private void makeBreakEffect() {
        this.playSound(SoundEvents.BLOCK_GLASS_BREAK, 0.03f, 1.1f);
        ((ServerWorld)this.getWorld()).spawnParticles(
                new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.ICE.getDefaultState()),
                this.getX(),
                this.getY(),
                this.getZ(),
                10,
                0.1,
                0.1,
                0.1,
                0.01
        );
    }

    private void damageEntity(LivingEntity entity) {
        var damageSource = entity.getDamageSources().create(entity.getDamageSources().freeze().getTypeRegistryEntry().getKey().get(), this.getOwner());
        float damage = BASE_DAMAGE;

        if(entity.getWorld().getBiome(entity.getBlockPos()).value().isCold(entity.getBlockPos())) {
            damage = damage * 2;
        }

        if(castedWithIceTotem) {
            damage = damage * 2;
        }

        entity.damage(damageSource, damage);
    }

    @Override
    public EntityType<?> getPolymerEntityType(ServerPlayerEntity player) {
        return EntityType.SNOWBALL;
    }

    @Override
    public void modifyRawTrackedData(List<DataTracker.SerializedEntry<?>> data, ServerPlayerEntity player, boolean initial) {
        data.removeIf(serializedEntry -> serializedEntry.handler().equals(ITEM.dataType()));
        data.add(new DataTracker.SerializedEntry<>(ITEM.id(), ITEM.dataType(), this.getDefaultItem().getDefaultStack()));
    }
}
