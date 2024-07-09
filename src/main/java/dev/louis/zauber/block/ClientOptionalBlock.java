package dev.louis.zauber.block;

import dev.louis.zauber.Zauber;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.core.api.utils.PolymerClientDecoded;
import eu.pb4.polymer.core.api.utils.PolymerKeepModel;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;

public class ClientOptionalBlock extends Block implements PolymerBlock, PolymerKeepModel, PolymerClientDecoded {
    private final BlockState polymerBlockState;

    public ClientOptionalBlock(Settings settings, Block polymerBlockState) {
        super(settings);
        this.polymerBlockState = polymerBlockState.getDefaultState();
    }


    @Override
    public BlockState getPolymerBlockState(BlockState state, ServerPlayerEntity player) {
        return Zauber.isClientModded(player) ? state : polymerBlockState;
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state) {
        return polymerBlockState;
    }
}
