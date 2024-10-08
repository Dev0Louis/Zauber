package dev.louis.zauber.entity;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class AreaEffectSpellEntity extends Entity {
    public static final EntityType<AreaEffectSpellEntity> TYPE = EntityType.Builder
            .<AreaEffectSpellEntity>create(AreaEffectSpellEntity::new, SpawnGroup.MISC)
            .dimensions(3, 1)
            .build();

    public enum Type {
        ICE(ParticleTypes.SNOWFLAKE) {
            @Override
            public void affectBlock(World world, BlockPos pos) {
                var blockState = world.getBlockState(pos);
                if (blockState.isReplaceable() && blockState.getFluidState().isIn(FluidTags.WATER)) {
                    world.setBlockState(pos, Blocks.FROSTED_ICE.getDefaultState(), Block.NOTIFY_ALL);
                }
            }

            @Override
            public void affectEntity(World world, Entity entity) {
                entity.setVelocity(Vec3d.ZERO);
                entity.velocityModified = true;
                entity.setFrozenTicks(100);
                entity.extinguishWithSound();
            }
        },
        FIRE(ParticleTypes.FLAME) {
            @Override
            public void affectBlock(World world, BlockPos pos) {
                var blockState = world.getBlockState(pos);
                if (blockState.isOf(Blocks.TNT)) {
                    spawnTnt(world, pos);
                    world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL_AND_REDRAW);
                }
            }

            private static void spawnTnt(World world, BlockPos pos) {
                if (!world.isClient()) {
                    TntEntity tntEntity = new TntEntity(world, (double) pos.getX() + 0.5, pos.getY(), (double) pos.getZ() + 0.5, null);
                    world.spawnEntity(tntEntity);
                    world.playSound(null, tntEntity.getX(), tntEntity.getY(), tntEntity.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    world.emitGameEvent(null, GameEvent.PRIME_FUSE, pos);
                }
            }

            @Override
            public void affectEntity(World world, Entity entity) {
                entity.setVelocity(Vec3d.ZERO);
                entity.velocityModified = true;
                entity.setFrozenTicks(100);
                entity.extinguishWithSound();
            }
        };

        public final ParticleEffect particleEffect;

        Type(ParticleEffect particleEffect) {
            this.particleEffect = particleEffect;
        }

        public abstract void affectBlock(World world, BlockPos pos);
        public abstract void affectEntity(World world, Entity entity);

    }

    //Default type
    private Type type = Type.ICE;

    public AreaEffectSpellEntity(EntityType<? extends AreaEffectSpellEntity> entityType, World world) {
        super(entityType, world);
    }

    public AreaEffectSpellEntity(World world) {
        super(TYPE, world);
    }

    @Override
    public void tick() {
        if (age > 80) {
            this.discard();
            return;
        }

        var box = this.getBoundingBox();
        BlockPos.stream(box).forEach(blockPos -> type.affectBlock(this.getWorld(), blockPos));
        this.getWorld().getOtherEntities(this, box).stream().filter(LivingEntity.class::isInstance).map(LivingEntity.class::cast).forEach(this::affect);
    }

    private void affect(LivingEntity entity) {
        if (entity.isAlive() && entity.isMobOrPlayer()) {
            entity.damage(this.getDamageSource(), 1);
            type.affectEntity(this.getWorld(), entity);
        }
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        var string = nbt.getString("spellEffectType");
        try {
            type = Type.valueOf(string);
        } catch (IllegalArgumentException ignored) {

        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putString("spellEffectType", type.name());
    }

    public DamageSource getDamageSource() {
        return this.getWorld().getDamageSources().freeze();
    }
}
