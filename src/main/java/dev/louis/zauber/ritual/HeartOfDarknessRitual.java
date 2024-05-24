package dev.louis.zauber.ritual;

import dev.louis.zauber.block.DarknessAccumulatorBlock;
import dev.louis.zauber.block.entity.RitualStoneBlockEntity;
import dev.louis.zauber.helper.ParticleHelper;
import dev.louis.zauber.helper.SoundHelper;
import dev.louis.zauber.item.HeartOfTheDarknessItem;
import dev.louis.zauber.item.ZauberItems;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HeartOfDarknessRitual extends Ritual {
    private static final Vector3f BLACK_PARTICLE_COLOR = new Vector3f(0, 0, 0);
    private List<BlockPos> darknessAccumulators;
    @Nullable
    private BlockPos nextAccumulatorPos;
    private int collectedDarkness;
    private int intenseParticleSpawnTicks;

    public HeartOfDarknessRitual(World world, RitualStoneBlockEntity ritualStoneBlockEntity, List<BlockPos> darknessAccumulators) {
        super(world, ritualStoneBlockEntity);
        this.darknessAccumulators = darknessAccumulators;
    }

    @Override
    public void tick() {
        if (this.nextAccumulatorPos == null) {
            if (this.age % 5 == 0 && this.darknessAccumulators.isEmpty()) {
                this.darknessAccumulators = ritualStoneBlockEntity.getFilledDarknessAccumulators().collect(Collectors.toList());
            }

            this.darknessAccumulators.removeIf(blockPos -> !world.getBlockState(blockPos).get(DarknessAccumulatorBlock.HAS_DARKNESS));
            this.darknessAccumulators.stream().findAny().ifPresent(blockPos -> this.nextAccumulatorPos = blockPos);
        }

        if (this.age % 5 == 0) {
            if (nextAccumulatorPos != null) {
                ParticleHelper.spawnParticleLine(
                        (ServerWorld) world,
                        this.pos.toCenterPos().add(0, 0.5, 0),
                        nextAccumulatorPos.toCenterPos(),
                        new DustParticleEffect(BLACK_PARTICLE_COLOR, 0.7f),
                        16
                );
            }

            if (this.collectedDarkness >= 1) {
                ParticleHelper.spawnParticle(
                        (ServerWorld) world,
                        pos.toCenterPos().add(0, 0.9, 0),
                        new DustParticleEffect(BLACK_PARTICLE_COLOR, 0.3f)
                );
            }
        }
        if (this.intenseParticleSpawnTicks-- > 0) {
            ParticleHelper.spawnParticle(
                    (ServerWorld) world,
                    pos.toCenterPos().add(0, 0.9, 0),
                    new DustParticleEffect(BLACK_PARTICLE_COLOR, 0.7f)
            );
        }
        if (this.age % 30 == 0) {
            if (nextAccumulatorPos == null) {
                //Fail Ritual
                return;
            }
            var pos = nextAccumulatorPos;
            var state = world.getBlockState(pos);

            if (state.get(DarknessAccumulatorBlock.HAS_DARKNESS)) {
                this.nextAccumulatorPos = null;
                this.collectedDarkness++;
                this.intenseParticleSpawnTicks = 5;
                this.world.setBlockState(pos, state.with(DarknessAccumulatorBlock.HAS_DARKNESS, false));
            }

            SoundHelper.playSound(
                    (ServerWorld) world,
                    pos,
                    SoundEvents.ITEM_BUCKET_EMPTY_LAVA,
                    SoundCategory.BLOCKS,
                    1,
                    -1
            );

            this.darknessAccumulators.remove(pos);
        }
    }

    @Override
    public void onStart() {

    }

    @Override
    public void finish() {

        if (this.ritualStoneBlockEntity.getStoredStack().isOf(Items.HEART_OF_THE_SEA) && collectedDarkness >= 4) {
            this.ritualStoneBlockEntity.setStoredStack(ItemStack.EMPTY);
            world.playSound(null, this.pos, SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.PLAYERS, 1, 4);
            world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, ZauberItems.HEART_OF_THE_DARKNESS.getDefaultStack(), 0, 0.3f, 0));
        } else {
            HeartOfTheDarknessItem.onDisappeared((ServerWorld) world, pos.toCenterPos());
        }
        //darknessAccumulators.forEach(pos -> world.setBlockState(pos, world.getBlockState(pos).with(DarknessAccumulatorBlock.HAS_DARKNESS, false)));
    }

    @Override
    public boolean shouldStop() {
        return this.age > 200;
    }

    @Override
    public Stream<Position> getConnections() {
        return Stream.empty();
    }

    public static Ritual tryStart(World world, RitualStoneBlockEntity ritualStoneBlockEntity) {
        var ritualItemStack = ritualStoneBlockEntity.getStoredStack();

        var darknessAccumulators = ritualStoneBlockEntity.getFilledDarknessAccumulators().collect(Collectors.toList());
        if(!(ritualItemStack.isOf(Items.HEART_OF_THE_SEA)) || darknessAccumulators.isEmpty()) return null;
        return new HeartOfDarknessRitual(world, ritualStoneBlockEntity, darknessAccumulators);
    }
}
