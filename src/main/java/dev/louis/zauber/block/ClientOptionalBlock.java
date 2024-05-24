package dev.louis.zauber.block;

import dev.louis.zauber.Zauber;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.core.api.utils.PolymerClientDecoded;
import eu.pb4.polymer.core.api.utils.PolymerKeepModel;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;

public class ClientOptionalBlock extends Block implements PolymerBlock, PolymerKeepModel, PolymerClientDecoded {
    private final Block polymerBlock;

    public ClientOptionalBlock(Settings settings, Block polymerBlock) {
        super(settings);
        this.polymerBlock = polymerBlock;
    }

    @Override
    public Block getPolymerBlock(BlockState state) {
        return polymerBlock;
    }

    @Override
    public Block getPolymerBlock(BlockState state, ServerPlayerEntity player) {
        return Zauber.isClientModded(player) ? this : polymerBlock;
    }
}
