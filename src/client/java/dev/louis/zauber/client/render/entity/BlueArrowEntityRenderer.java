package dev.louis.zauber.client.render.entity;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.Identifier;

public class BlueArrowEntityRenderer extends ProjectileEntityRenderer<PersistentProjectileEntity> {
    public static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/projectiles/arrow.png");

    public BlueArrowEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(PersistentProjectileEntity entity) {
        return TEXTURE;
    }


    @Override
    public void vertex(
            MatrixStack.Entry matrix,
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
        vertexConsumer
                .vertex(matrix, x, y, z)
                .color(0, 0, 255, 100)
                .texture(u, v)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(light)
                .normal(matrix, normalX, normalY, normalZ);
    }

    public RenderLayer getRenderLayer(Identifier texture) {
        return RenderLayer.getEntityTranslucentCull(texture);
    }
}
