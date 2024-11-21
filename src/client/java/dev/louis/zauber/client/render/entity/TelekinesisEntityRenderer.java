package dev.louis.zauber.client.render.entity;

import dev.louis.zauber.entity.TelekinedBlockEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class TelekinesisEntityRenderer extends EntityRenderer<TelekinedBlockEntity, TelekinedBlockRenderState> {
    private final BlockRenderManager blockRenderManager;

    public TelekinesisEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.blockRenderManager = ctx.getBlockRenderManager();
    }

    @Override
    public TelekinedBlockRenderState createRenderState() {
        return new TelekinedBlockRenderState();
    }


    @Override
    public void render(TelekinedBlockRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        var blockState = state.blockState;
        if (blockState != null) {
            matrices.push();
            matrices.translate(-.5, 0, -.5);
            this.blockRenderManager
                    .getModelRenderer()
                    .render(
                            matrices.peek(),
                            vertexConsumers.getBuffer(RenderLayers.getMovingBlockLayer(blockState)),
                            blockState,
                            this.blockRenderManager.getModel(blockState),
                            1,
                            1,
                            1,
                            light,
                            0
                    );
            matrices.pop();
        }
    }
}
