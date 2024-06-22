package dev.louis.zauber.client.render.entity;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.Identifier;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.Arrays;

public class BlueArrowEntityRenderer extends ProjectileEntityRenderer<PersistentProjectileEntity> {
    public static final Identifier TEXTURE = new Identifier("textures/entity/projectiles/arrow.png");

    public BlueArrowEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(PersistentProjectileEntity persistentProjectileEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        DebugRenderer.drawString(matrixStack, vertexConsumerProvider, Arrays.stream(persistentProjectileEntity.getClass().getFields()).filter(field -> field.getType() == boolean.class).filter(field -> field.getName().equals("inGround")).findFirst().map(field -> {
            try {
                field.setAccessible(true);
                return String.valueOf(field.getBoolean(persistentProjectileEntity));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }).orElse("NONO"), persistentProjectileEntity.getX(), persistentProjectileEntity.getY() + 1, persistentProjectileEntity.getZ(), 0xFF0000);
        super.render(persistentProjectileEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Identifier getTexture(PersistentProjectileEntity entity) {
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
                .color(0, 0, 255, 100)
                .texture(u, v)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(light)
                .normal(normalMatrix, (float)normalX, (float)normalY, (float)normalZ)
                .next();
    }

    public RenderLayer getRenderLayer(Identifier texture) {
        return RenderLayer.getEntityTranslucentCull(texture);
    }
}
