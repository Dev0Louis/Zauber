package dev.louis.zauber.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.louis.zauber.client.render.misc.ZauberRenderLayers;
import dev.louis.zauber.extension.EntityExtension;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true)
@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Inject(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V")
    )
    public void renderTelekinesisAroundTelekinesed(Entity entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int light, CallbackInfo ci, @Local EntityRenderer<Entity> entityRenderer) {
        if (((EntityExtension) entity).isTelekinesed()) {
            VertexConsumerProvider fakeVertexConsumerProvider = (layer) -> vertexConsumerProvider.getBuffer(ZauberRenderLayers.getBrrrrrrrr(entity, tickDelta));

            entityRenderer.render(
                    entity,
                    yaw,
                    tickDelta,
                    matrices,
                    fakeVertexConsumerProvider,
                    light
            );
        }
    }
}
