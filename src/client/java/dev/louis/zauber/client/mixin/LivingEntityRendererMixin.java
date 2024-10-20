package dev.louis.zauber.client.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.louis.zauber.client.render.entity.RGBAEntityRenderer;
import dev.louis.zauber.client.render.misc.ZauberRenderLayers;
import dev.louis.zauber.extension.EntityExtension;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Consumer;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {

    @Shadow protected EntityModel<?> model;

    @Shadow protected abstract float getAnimationCounter(LivingEntity entity, float tickDelta);

    @ModifyArg(
            method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V"),
            index = 4
    )
    public int modifyColor(int color, @Local(argsOnly = true) LivingEntity entity) {
        if (this instanceof RGBAEntityRenderer customRGBAEntity) {
            return customRGBAEntity.getColor();
        }
        return color;
    }


    @ModifyArg(
            method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V"),
            index = 3
    )
    public int modifyOverlay(int overlay, @Local(argsOnly = true) LivingEntity entity) {
        if (this instanceof RGBAEntityRenderer customRGBAEntity) {
            return customRGBAEntity.getOverlay();
        }

        return overlay;
    }



    /*@Inject(
            method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V", shift = At.Shift.AFTER)
    )
    public void renderTelekinesisEffect(LivingEntity livingEntity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int light, CallbackInfo ci) {
        if (((EntityExtension) livingEntity).isTelekinesed()) {
            matrices.push();
            var vertexConsumer = vertexConsumerProvider.getBuffer(ZauberRenderLayers.getBrrrrrrrr(true));
            var overlay = OverlayTexture.packUv(OverlayTexture.getU(this.getAnimationCounter(livingEntity, tickDelta)), OverlayTexture.getV(false));;

            this.model.render(
                    matrices,
                    vertexConsumer,
                    light,
                    overlay,
                    -1
            );
            matrices.pop();
        }
    }

    @WrapOperation(
            method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/feature/FeatureRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/Entity;FFFFFF)V")
    )
    public void renderTelekinesisEffectFeatures(
            FeatureRenderer renderer,
            MatrixStack matrixStack,
            VertexConsumerProvider vertexConsumerProvider,
            int light,
            Entity entity,
            float limbAngle,
            float limbDistance,
            float tickDelta,
            float animationProgress,
            float headYaw,
            float headPitch,
            Operation<Void> original
    ) {
        original.call(renderer, matrixStack, vertexConsumerProvider, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
        if (((EntityExtension) entity).isTelekinesed()) {

            VertexConsumerProvider fakeVertexConsumerProvider = (layer) -> vertexConsumerProvider.getBuffer(ZauberRenderLayers.getBrrrrrrrr(true));
            original.call(renderer, matrixStack, fakeVertexConsumerProvider, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
        }
    }*/


    /*@ModifyArg(
            method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"),
            index = 7
    )
    public float modifyAlpha(float alpha, @Local(argsOnly = true) LivingEntity entity) {
        if (this instanceof RGBAEntityRenderer customRGBAEntity) {
            return customRGBAEntity.getAlpha();
        }
        return alpha;
    }*/
}
