package dev.louis.zauber.client.mixin;

import dev.louis.zauber.client.extension.BlockRenderManagerExtension;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockRenderManager.class)
public abstract class BlockRenderManagerMixin implements BlockRenderManagerExtension {
    @Shadow public abstract BakedModel getModel(BlockState state);

    @Shadow @Final private BlockColors blockColors;

    @Shadow @Final private BlockModelRenderer blockModelRenderer;

    @Shadow @Final private BuiltinModelItemRenderer builtinModelItemRenderer;

    public void zauber$renderWorldBlockAsEntity(World sourceWorld, BlockPos sourcePos, BlockState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        BlockRenderType blockRenderType = state.getRenderType();
        if (blockRenderType != BlockRenderType.INVISIBLE) {
            switch (blockRenderType) {
                case MODEL:
                    BakedModel bakedModel = this.getModel(state);
                    int i = this.blockColors.getColor(state, sourceWorld, sourcePos, 0);
                    float f = (float)(i >> 16 & 0xFF) / 255.0F;
                    float g = (float)(i >> 8 & 0xFF) / 255.0F;
                    float h = (float)(i & 0xFF) / 255.0F;
                    this.blockModelRenderer
                            .render(matrices.peek(), vertexConsumers.getBuffer(RenderLayers.getEntityBlockLayer(state, false)), state, bakedModel, f, g, h, light, overlay);
                    break;
                case ENTITYBLOCK_ANIMATED:
                    this.builtinModelItemRenderer.render(new ItemStack(state.getBlock()), ModelTransformationMode.NONE, matrices, vertexConsumers, light, overlay);
            }
        }
    }
}
