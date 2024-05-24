package dev.louis.zauber.block;

import com.mojang.serialization.MapCodec;
import dev.louis.zauber.Zauber;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.core.api.utils.PolymerClientDecoded;
import eu.pb4.polymer.core.api.utils.PolymerKeepModel;
import eu.pb4.polymer.virtualentity.api.BlockWithElementHolder;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.elements.BlockDisplayElement;
import net.minecraft.block.*;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class ExtinguishedTorchBlock extends AbstractTorchBlock implements PolymerBlock, PolymerKeepModel, PolymerClientDecoded, BlockWithElementHolder {
    public static final MapCodec<ExtinguishedTorchBlock> CODEC = createCodec(ExtinguishedTorchBlock::new);

    protected ExtinguishedTorchBlock(Settings settings) {
        super(settings);
    }

    public MapCodec<? extends ExtinguishedTorchBlock> getCodec() {
        return CODEC;
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state, ServerPlayerEntity player) {
        if(Zauber.isClientModded(player)) return state;
        return Blocks.REDSTONE_TORCH.getDefaultState().with(RedstoneTorchBlock.LIT, false);
    }

    @Override
    public Block getPolymerBlock(BlockState state, ServerPlayerEntity player) {
        if(Zauber.isClientModded(player)) return this;
        return Blocks.REDSTONE_TORCH;
    }

    @Override
    public Block getPolymerBlock(BlockState state) {
        return Blocks.REDSTONE_TORCH;
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        return new CustomHolder();
    }

    public static class CustomHolder extends ElementHolder {
        @Override
        public boolean startWatching(ServerPlayNetworkHandler player) {
            if (!Zauber.isClientModded(player.getPlayer())) return super.startWatching(player);
            return false;
        }

        public CustomHolder() {
            var coal = this.addElement(new BlockDisplayElement(Blocks.COAL_BLOCK.getDefaultState()));
            final float xSize = 0.13f;

            coal.setScale(new Vector3f(xSize));
            coal.setOffset(new Vec3d(-xSize / 2, 0, -xSize / 2));
        }

    }
}
