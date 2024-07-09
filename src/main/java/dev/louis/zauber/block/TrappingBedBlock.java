package dev.louis.zauber.block;

import dev.louis.zauber.helper.ShutUpAboutBlockStateModels;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.BedPart;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

/**
 * The Trapping Bed traps you in a sleep, that you would never wish to end...
 */
@ShutUpAboutBlockStateModels
public class TrappingBedBlock extends BedBlock implements PolymerBlock {
    public TrappingBedBlock(DyeColor color, Settings settings) {
        super(color, settings);
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state) {
        return Blocks.BLACK_BED.getDefaultState().with(FACING, state.get(FACING)).with(OCCUPIED, state.get(OCCUPIED)).with(PART, state.get(PART));
    }

    public static BlockState getStateFor(BlockState state) {
        return ZauberBlocks.TRAPPING_BED.getDefaultState().with(FACING, state.get(FACING)).with(OCCUPIED, state.get(OCCUPIED)).with(PART, state.get(PART));
    }

    @Override
    public boolean handleMiningOnServer(ItemStack tool, BlockState state, BlockPos pos, ServerPlayerEntity player) {
        return false;
    }

    public static Direction getDirectionTowardsOtherPart(BedPart part, Direction direction) {
        return part == BedPart.FOOT ? direction : direction.getOpposite();
    }
}
