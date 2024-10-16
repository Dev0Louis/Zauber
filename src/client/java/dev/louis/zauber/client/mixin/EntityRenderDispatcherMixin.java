package dev.louis.zauber.client.mixin;

import dev.louis.zauber.client.render.misc.SphereRenderer;
import dev.louis.zauber.client.render.misc.ZauberRenderLayers;
import dev.louis.zauber.extension.EntityExtension;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Inject(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderer;render(Lnet/minecraft/entity/Entity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V")
    )
    public void renderTelekenisisAroundTelekenised(Entity entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (((EntityExtension) entity).isTelekinesed()) {
            matrices.push();
            //matrices.translate(0, .15, 0);
            var scale = 1f;
            matrices.translate(0, scale / 2, 0);
            matrices.scale(scale, scale, scale);
            matrices.multiply(RotationAxis.NEGATIVE_Z.rotation((float) Math.sin(entity.age / 100f * Math.PI)));
            matrices.multiply(RotationAxis.POSITIVE_X.rotation((float) Math.sin(entity.age / 50f * Math.PI)));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotation((float) Math.sin(entity.age / 300f * Math.PI)));
            SphereRenderer.renderSphere(matrices.peek(), vertexConsumers.getBuffer(ZauberRenderLayers.getBrrrrrrrr()));
            matrices.pop();
        }
    }
}
