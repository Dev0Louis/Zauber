package dev.louis.zauber.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.louis.zauber.client.render.misc.ZauberRenderLayers;
import dev.louis.zauber.extension.EntityExtension;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
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
            method = "render(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V")
    )
    public void renderTelekinesisAroundTelekinesed(Entity entity, double x, double y, double z, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, EntityRenderer<? super Entity, EntityRenderState> renderer, CallbackInfo ci, @Local(ordinal = 0) EntityRenderState state) {
        if (entity.zauber$isTelekinesed()) {
            VertexConsumerProvider fakeVertexConsumerProvider = (layer) -> {
                if (!layer.toString().equals("leash")) {
                    return vertexConsumers.getBuffer(ZauberRenderLayers.getBrrrrrrrr(entity, tickDelta));
                }
                return vertexConsumers.getBuffer(layer);
            };

            renderer.render(
                    state,
                    matrices,
                    fakeVertexConsumerProvider,
                    light
            );
        }
    }
}
