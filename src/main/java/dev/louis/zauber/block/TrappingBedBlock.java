package dev.louis.zauber.block;

import dev.louis.zauber.helper.ShutUpAboutBlockStateModels;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.BedPart;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Direction;

/**
 * The Trapping Bed traps you in a sleep, that you would never wish to end...
 */
@ShutUpAboutBlockStateModels
public class TrappingBedBlock extends BedBlock {
    public TrappingBedBlock(DyeColor color, Settings settings) {
        super(color, settings);
    }


    public static BlockState getStateFor(BlockState state) {
        return ZauberBlocks.TRAPPING_BED.getDefaultState().with(FACING, state.get(FACING)).with(OCCUPIED, state.get(OCCUPIED)).with(PART, state.get(PART));
    }



    public static Direction getDirectionTowardsOtherPart(BedPart part, Direction direction) {
        return part == BedPart.FOOT ? direction : direction.getOpposite();
    }
}
