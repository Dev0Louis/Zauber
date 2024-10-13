package dev.louis.zauber.client.render.entity;

import dev.louis.zauber.entity.BlockTelekinesisEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

public class TelekinesisEntityRenderer extends EntityRenderer<BlockTelekinesisEntity> {
    private final BlockRenderManager blockRenderManager;

    public TelekinesisEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.blockRenderManager = ctx.getBlockRenderManager();
    }

    @Override
    public void render(BlockTelekinesisEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        var blockState = entity.getBlockState();
        if (blockState != null) {
            matrices.push();
            matrices.translate(-.5, 0, -.5);
            this.blockRenderManager
                    .getModelRenderer()
                    .render(
                            entity.getWorld(),
                            this.blockRenderManager.getModel(blockState),
                            blockState,
                            entity.getBlockPos(),
                            matrices,
                            vertexConsumers.getBuffer(RenderLayers.getMovingBlockLayer(blockState)),
                            false,
                            Random.create(),
                            blockState.getRenderingSeed(entity.getFallingBlockPos()),
                            OverlayTexture.DEFAULT_UV
                    );
            matrices.pop();
        }
    }

    @Override
    public Identifier getTexture(BlockTelekinesisEntity entity) {
        return null;
    }
}
