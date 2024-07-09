package dev.louis.zauber.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.louis.zauber.client.render.entity.RGBAEntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {

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
