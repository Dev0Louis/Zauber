package dev.louis.zauber.block.entity;

import dev.louis.zauber.block.DarknessAccumulatorBlock;
import dev.louis.zauber.block.ZauberBlocks;
import dev.louis.zauber.helper.ParticleHelper;
import dev.louis.zauber.item.HeartOfTheDarknessItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.joml.Vector3f;

public class DarknessAccumulatorBlockEntity extends BlockEntity {
    public static final Vector3f COLOR = new Vector3f(0, 0, 0);
    public static final BlockEntityType<DarknessAccumulatorBlockEntity> TYPE = BlockEntityType.Builder.create(DarknessAccumulatorBlockEntity::new, ZauberBlocks.DARKNESS_ACCUMULATOR).build(null);

    public DarknessAccumulatorBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE, pos, state);
    }


    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.getTime() % 20L == 0L) {
            if (world.getLightLevel(pos) > HeartOfTheDarknessItem.MAX_BRIGHTNESS) {
                world.setBlockState(pos, state.with(DarknessAccumulatorBlock.HAS_DARKNESS, false));
            }
        }
        if (world.getBaseLightLevel(pos, 0) == 0 && !state.get(DarknessAccumulatorBlock.HAS_DARKNESS)) {
            ParticleHelper.spawnParticle(
                    (ServerWorld) world,
                    pos.toCenterPos(),
                    0.1f,
                    0,
                    //ParticleTypes.MYCELIUM
                    new DustParticleEffect(COLOR, 2.6f)
            );
        }
    }
}
