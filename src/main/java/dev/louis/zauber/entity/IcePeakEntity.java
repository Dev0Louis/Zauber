package dev.louis.zauber.entity;

import dev.louis.zauber.helper.ParticleHelper;
import dev.louis.zauber.helper.SoundHelper;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Arrays;

public class IcePeakEntity extends StructureEntity {
    public static BlockState[][][] ICE_SPIKE_ARRAY = createEmptyArray();
    public static final EntityType<IcePeakEntity> TYPE = FabricEntityTypeBuilder
            .create(SpawnGroup.MISC, IcePeakEntity::new)
            .build();
    private static final int TICKS_TO_GROW_FULL = 30;
    private static final float FINAL_SIZE = 1;
    private static final int delay = 20;

    public IcePeakEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public float getSize() {
        int progress = this.getProgress();
        if (progress < 0) return 0;

        return Math.min(FINAL_SIZE / TICKS_TO_GROW_FULL * progress, FINAL_SIZE);
    }

    public int getProgress() {
        return age - delay;
    }

    @Override
    public BlockState[][][] getBlockStateArray() {
        return ICE_SPIKE_ARRAY;
    }

    @Override
    public void tick() {
        // DEBUG CODE
        /*Debugger.addEntityBoundBox(this, List.of(
                new Pair<>(this.getPushBox(), new Color(100, 0, 0)),
                new Pair<>(this.getFullBox(), new Color(0, 100, 0))
        ));*/

        if (isComplete()) {
            this.onBreaking();
            this.discard();
        }
        if (age % 4 == 0) {
            ParticleHelper.spawnParticles(
                    (ServerWorld) this.getWorld(),
                    this.getPos(),
                    new ItemStackParticleEffect(ParticleTypes.ITEM, Items.ICE.getDefaultStack()),
                    30,
                    0.2f,
                    0.3f
            );
            if (this.getProgress() < 0) {
                SoundHelper.playSound(
                        (ServerWorld) this.getWorld(),
                        this.getPos(),
                        SoundEvents.BLOCK_GLASS_BREAK,
                        SoundCategory.AMBIENT,
                        2,
                        -1
                );
                this.getWorld().getEntitiesByClass(LivingEntity.class, Box.from(this.getPos()), EntityPredicates.EXCEPT_SPECTATOR).forEach(livingEntity -> {
                    livingEntity.setFrozenTicks(200);
                });
            }
        }

        if (stopsPushing()) return;
        var pushBox = this.getPushBox();
        World world = this.getWorld();
        world.getEntitiesByClass(
                Entity.class,
                this.getFullBox(),
                EntityPredicates.EXCEPT_SPECTATOR
        ).forEach(entity -> {
            Vec3d velocity;
            //We check the progress here as the push box is really jumpy at first
            if (pushBox.contains(entity.getPos()) || this.getProgress() < 5) {
                entity.damage(world.getDamageSources().freeze(), 10);
                velocity = new Vec3d(0, 6, 0);
            } else {
                //In box, but not in push box
                velocity = entity.getPos().subtract(this.getPos()).normalize().multiply(1, 0.2, 1);
            }
            SoundHelper.playSound(
                    (ServerWorld) this.getWorld(),
                    entity.getPos(),
                    SoundEvents.BLOCK_GLASS_STEP, SoundCategory.AMBIENT, 4, 1
            );
            entity.setFrozenTicks(200);
            entity.setVelocity(velocity);

            //entity.getPos().subtract(this.getPos()).normalize().multiply(2, 6, 2)
            entity.velocityModified = true;

        });
    }

    private void onBreaking() {
        int maxY = ICE_SPIKE_ARRAY[0].length;
        for (int y = 0; y < maxY; y++) {
            ParticleHelper.spawnParticles(
                    (ServerWorld) this.getWorld(),
                    this.getPos().add(0, y, 0),
                    new ItemStackParticleEffect(ParticleTypes.ITEM, Items.ICE.getDefaultStack()),
                    30,
                    3,
                    1
            );

            ParticleHelper.spawnParticles(
                    (ServerWorld) this.getWorld(),
                    this.getPos().add(0, y, 0),
                    ParticleTypes.GUST,
                    2,
                    1,
                    1
            );
        }
        SoundHelper.playSound(
                (ServerWorld) this.getWorld(),
                this.getPos().add(0, maxY / 2f, 0),
                SoundEvents.BLOCK_GLASS_BREAK,
                SoundCategory.AMBIENT,
                5,
                -1
        );
    }

    public Box getPushBox() {
        float size = this.getSize();
        float x = Math.max(ICE_SPIKE_ARRAY.length * size, 2);
        float y = ICE_SPIKE_ARRAY[0].length * size;
        float z = Math.max(ICE_SPIKE_ARRAY[0][0].length * size, 2);
        float ySize = 7 * size;
        return Box.of(new Vec3d(0.5f, y - ySize / 2, 0.5f), x, ySize, z).offset(this.getBlockPos());
    }

    public Box getFullBox() {
        float size = this.getSize();
        float x = ICE_SPIKE_ARRAY.length * size;
        float y = ICE_SPIKE_ARRAY[0].length * size;
        float z = ICE_SPIKE_ARRAY[0][0].length * size;
        return Box.of(new Vec3d(0.5f, y / 2, 0.5f), x, y, z).offset(this.getBlockPos());
    }

    public boolean isComplete() {
        return this.getProgress() > TICKS_TO_GROW_FULL + 10;
    }

    public boolean stopsPushing() {
        return this.getProgress() > TICKS_TO_GROW_FULL - 1;
    }

    public static BlockState[][][] createEmptyArray() {
        BlockState[][][] blockStates = new BlockState[3][3][3];
        Arrays.fill(blockStates[0][0], Blocks.STRUCTURE_BLOCK.getDefaultState());
        Arrays.fill(blockStates[0], blockStates[0][0]);
        Arrays.fill(blockStates, blockStates[0]);

        return blockStates;
    }
}
