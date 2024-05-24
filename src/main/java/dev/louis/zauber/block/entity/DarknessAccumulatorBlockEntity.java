package dev.louis.zauber.block.entity;

import dev.louis.zauber.block.DarknessAccumulatorBlock;
import dev.louis.zauber.block.ZauberBlocks;
import dev.louis.zauber.item.HeartOfTheDarknessItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DarknessAccumulatorBlockEntity extends BlockEntity {
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
    }
}
