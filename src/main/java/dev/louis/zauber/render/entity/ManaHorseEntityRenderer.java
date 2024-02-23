package dev.louis.zauber.render.entity;

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


public class ManaHorseEntityRenderer extends HorseEntityRenderer {

    public ManaHorseEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    public final Identifier octagram = new Identifier(Zauber.MOD_ID, "textures/symbol/octagram.png");
    public final Identifier spell_rings = new Identifier(Zauber.MOD_ID, "textures/symbol/spell_rings.png");
    public final Identifier spell2 = new Identifier(Zauber.MOD_ID, "textures/symbol/spell2.png");
    public final Identifier spell3 = new Identifier(Zauber.MOD_ID, "textures/symbol/spell3.png");
    public final Identifier spell4 = new Identifier(Zauber.MOD_ID, "textures/symbol/spell4.png");


    @Override
    public void render(HorseEntity mobEntity, float f, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
        super.render(mobEntity, f, tickDelta, matrixStack, vertexConsumerProvider, light);
        matrixStack.push();
        float lerpAngleDegrees = MathHelper.lerpAngleDegrees(tickDelta, mobEntity.prevBodyYaw, mobEntity.bodyYaw);
        float animationProgress = this.getAnimationProgress(mobEntity, tickDelta);

        this.setupTransforms(mobEntity, matrixStack, animationProgress, lerpAngleDegrees, tickDelta);


        renderSymbol(
                mobEntity,
                matrixStack,
                tickDelta,
                1,
                (float) (3 + Math.sin(mobEntity.age / 40f) * 0.2),
                0.1f,
                true,
                this.spell4
        );

        matrixStack.pop();
    }

    private void renderSymbol(HorseEntity mobEntity, MatrixStack matrixStack, float tickDelta, int height, float size, float frontOffset, boolean inverse, Identifier texture) {
        final float offset = size / 2;

        var mutable = new BlockPos.Mutable(mobEntity.getX(), mobEntity.getY(), mobEntity.getZ());
        var world = mobEntity.getWorld();
        boolean hasFoundBlock = false;

        for (int i = 0; i < 5; i++) {
            if (world.isOutOfHeightLimit(mutable)) break;
            var blockState = mobEntity.getWorld().getBlockState(mutable);
            var canSpellCircleRenderOn = blockState.isSolidSurface(world, mutable, mobEntity, Direction.UP);
            if(canSpellCircleRenderOn) {
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

        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(inverse ? -mobEntity.age :  mobEntity.age));
        matrixStack.translate(-offset, -offset, 0);
        matrixStack.scale(size, size, size);

        Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
        int alpha = (int) (255 / distanceToGround);
        buffer.vertex(positionMatrix, 0, 1, 0).color(0, 0, 255, alpha).texture(0f, 0f).next();
        buffer.vertex(positionMatrix, 0, 0, 0).color(0, 0, 255, alpha).texture(0f, 1f).next();
        buffer.vertex(positionMatrix, 1, 0, 0).color(0, 0, 255, alpha).texture(1f, 1f).next();
        buffer.vertex(positionMatrix, 1, 1, 0).color(0, 0, 255, alpha).texture(1f, 0f).next();

        RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.disableCull();
        RenderSystem.enableBlend();

        tessellator.draw();

        RenderSystem.disableBlend();
        RenderSystem.enableCull();
    }

    @Override
    protected boolean isShaking(HorseEntity entity) {
        return ((ManaHorseEntity)entity).willDisappearSoon();
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
}
