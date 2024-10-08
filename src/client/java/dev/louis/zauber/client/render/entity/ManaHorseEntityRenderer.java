package dev.louis.zauber.client.render.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.louis.zauber.Zauber;
import dev.louis.zauber.entity.ManaHorseEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.HorseEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.awt.*;


public class ManaHorseEntityRenderer extends HorseEntityRenderer implements RGBAEntityRenderer {

    public ManaHorseEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    public final Identifier spell4 = Identifier.of(Zauber.MOD_ID, "textures/symbol/spell4.png");


    @Override
    public void render(HorseEntity mobEntity, float f, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
        matrixStack.push();
        float lerpAngleDegrees = MathHelper.lerpAngleDegrees(tickDelta, mobEntity.prevBodyYaw, mobEntity.bodyYaw);
        float animationProgress = this.getAnimationProgress(mobEntity, tickDelta);

        this.setupTransforms(mobEntity, matrixStack, animationProgress, lerpAngleDegrees, tickDelta, mobEntity.getScale());


        renderSymbol(
                mobEntity,
                matrixStack,
                vertexConsumerProvider,
                tickDelta,
                1,
                //Lerp between the ticks
                (float) (3 + Math.sin(mobEntity.age / 40f) * 0.2),
                0.1f,
                this.spell4
        );

        matrixStack.pop();
        super.render(mobEntity, f, tickDelta, matrixStack, vertexConsumerProvider, light);
    }

    private void renderSymbol(HorseEntity mobEntity, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, float tickDelta, int height, float size, float frontOffset, Identifier texture) {
        final float offset = size / 2;

        var mutable = new BlockPos.Mutable(mobEntity.getX(), mobEntity.getY(), mobEntity.getZ());
        var world = mobEntity.getWorld();
        boolean hasFoundBlock = false;

        for (int i = 0; i < 5; i++) {
            if (world.isOutOfHeightLimit(mutable)) break;
            var blockState = mobEntity.getWorld().getBlockState(mutable);
            var canSpellCircleRenderOn = blockState.isSolidSurface(world, mutable, mobEntity, Direction.UP);
            if (canSpellCircleRenderOn) {
                hasFoundBlock = true;
                break;
            }
            mutable.set(mutable, Direction.DOWN);
        }
        if (!hasFoundBlock) return;


        double y = MathHelper.lerp(tickDelta, mobEntity.lastRenderY, mobEntity.getY());
        float distanceToGround = (float) (y - mutable.getY());
        if (distanceToGround > 5) return;
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90));
        matrixStack.translate(0, 0, -distanceToGround + 1.01f);

        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(mobEntity.age));
        matrixStack.translate(-offset, -offset, 0);
        matrixStack.scale(size, size, size);

        Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();

        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        int alpha = (int) (255 / distanceToGround);
        bufferBuilder.vertex(positionMatrix, 0, 1, 0).color(0, 0, 255, alpha).texture(0f, 0f);
        bufferBuilder.vertex(positionMatrix, 0, 0, 0).color(0, 0, 255, alpha).texture(0f, 1f);
        bufferBuilder.vertex(positionMatrix, 1, 0, 0).color(0, 0, 255, alpha).texture(1f, 1f);
        bufferBuilder.vertex(positionMatrix, 1, 1, 0).color(0, 0, 255, alpha).texture(1f, 0f);

        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.enableDepthTest();
        //RenderSystem.disableCull();
        //RenderSystem.enableBlend();

        BufferRenderer.draw(bufferBuilder.end());

        //RenderSystem.disableBlend();
        //RenderSystem.enableCull();
    }

    @Override
    protected boolean isShaking(HorseEntity entity) {
        return ((ManaHorseEntity) entity).willDisappearSoon();
    }

    @Nullable
    @Override
    protected RenderLayer getRenderLayer(HorseEntity entity, boolean showBody, boolean translucent, boolean showOutline) {
        return RenderLayer.getEntityTranslucentCull(this.getTexture(entity));
    }

    @Override
    public boolean shouldRender(HorseEntity mobEntity, Frustum frustum, double d, double e, double f) {
        return true;
    }

    @Override
    public int getOverlay() {
        return Color.WHITE.getRGB();
    }

    @Override
    public int getColor() {
        return 0xB20000FF;
    }
}
