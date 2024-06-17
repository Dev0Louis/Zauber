package dev.louis.zauber.client.render.entity;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.Identifier;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class BlueArrowEntityRenderer extends ProjectileEntityRenderer<ArrowEntity> {
    public static final Identifier TEXTURE = new Identifier("textures/entity/projectiles/arrow.png");

    public BlueArrowEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(ArrowEntity persistentProjectileEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(persistentProjectileEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Identifier getTexture(ArrowEntity entity) {
        return TEXTURE;
    }

    @Override
    public void vertex(
            Matrix4f positionMatrix,
            Matrix3f normalMatrix,
            VertexConsumer vertexConsumer,
            int x,
            int y,
            int z,
            float u,
            float v,
            int normalX,
            int normalZ,
            int normalY,
            int light
    ) {
        vertexConsumer.vertex(positionMatrix, x, y, z)
                .color(0, 0, 255, 10)
                .texture(u, v)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(light)
                .normal(normalMatrix, (float)normalX, (float)normalY, (float)normalZ)
                .next();
    }
}
