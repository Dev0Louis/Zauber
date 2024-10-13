package dev.louis.zauber.client.extension;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface BlockRenderManagerExtension {
    default void zauber$renderWorldBlockAsEntity(World sourceWorld, BlockPos sourcePos, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        zauber$renderWorldBlockAsEntity(sourceWorld, sourcePos, sourceWorld.getBlockState(sourcePos), matrices, vertexConsumers, light, overlay);
    }

    void zauber$renderWorldBlockAsEntity(World sourceWorld, BlockPos sourcePos, BlockState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay);
}
