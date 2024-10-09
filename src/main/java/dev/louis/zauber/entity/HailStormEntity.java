package dev.louis.zauber.entity;

import dev.louis.zauber.Zauber;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class HailStormEntity extends Entity {

    public static final EntityType<AreaSpellEffectEntity> TYPE = EntityType.Builder
            .<AreaSpellEffectEntity>create(AreaSpellEffectEntity::new, SpawnGroup.MISC)
            .dimensions(16, 1)
            .build();
    @Nullable
    private LivingEntity owner;

    public HailStormEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public HailStormEntity(World world, LivingEntity owner) {
        super(TYPE, world);
        this.owner = owner;
    }

    @Override
    public void tick() {
        if (age > 30) {
            this.discard();
            return;
        }

        World world = this.getWorld();
        final int size = 8;
        for (int x = -size; x < size; x += 2) {
            for (int z = -size; z < size; z += 2) {
                if (world.getRandom().nextFloat() < 0.75f) continue;
                HailStoneEntity hailStoneEntity = HailStoneEntity.TYPE.create(world);
                hailStoneEntity.setOwner(owner);
                hailStoneEntity.setPosition(this.getPos().add(x, 3 * world.getRandom().nextDouble(), z));
                Vec3d velocity = new Vec3d(world.getRandom().nextDouble() - .5, world.getRandom().nextDouble() - 1, world.getRandom().nextDouble() - .5).multiply(0.5);
                hailStoneEntity.setVelocity(velocity);
                hailStoneEntity.castedWithIceTotem = Zauber.hasTotem(this.owner, TotemOfIceEntity.TYPE);
                world.spawnEntity(hailStoneEntity);
            }
        }
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (this.getWorld() instanceof ServerWorld serverWorld && serverWorld.getEntity(nbt.getUuid("casterUUID")) instanceof LivingEntity livingEntity) {
            owner = livingEntity;
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putUuid("casterUUID", owner.getUuid());
    }
}
