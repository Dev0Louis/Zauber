package dev.louis.zauber.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.louis.zauber.client.render.item.StaffItemRenderer;
import dev.louis.zauber.client.render.item.UnsafeItemRendererContext;
import dev.louis.zauber.item.ZauberItems;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true)
@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @Shadow @Final private ItemModels models;

    @ModifyVariable(
            method = "getModel",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/BakedModel;getOverrides()Lnet/minecraft/client/render/model/json/ModelOverrideList;", shift = At.Shift.BEFORE, by = 2, ordinal = 0)
    )
    public BakedModel modifyBakedModel1(BakedModel model, @Local ItemStack stack) {
        if (stack.isOf(ZauberItems.STAFF)) {
            return this.models.getModelManager().getModel(StaffItemRenderer.STAFF_IN_HAND);
        }

        return model;
    }

    @ModifyVariable(
            method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z", ordinal = 0, shift = At.Shift.BEFORE, by = 2),
            argsOnly = true
    )
    public BakedModel a(BakedModel model, @Local(argsOnly = true) ItemStack itemStack) {
        if (itemStack.isOf(ZauberItems.STAFF)) {
            return this.models.getModelManager().getModel(StaffItemRenderer.STAFF);
        }
        return model;
    }


    @Inject(
            method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/world/World;III)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V")
    )
    public void setUnsafeItemRendererContext(LivingEntity entity, ItemStack item, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, World world, int light, int overlay, int seed, CallbackInfo ci) {
        UnsafeItemRendererContext.RENDERER_ENTITY.set(entity);
    }

    @Inject(
            method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/world/World;III)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V", shift = At.Shift.AFTER)
    )
    public void clearUnsafeItemRendererContext(LivingEntity entity, ItemStack item, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, World world, int light, int overlay, int seed, CallbackInfo ci) {
        UnsafeItemRendererContext.RENDERER_ENTITY.remove();
    }


    /*@ModifyExpressionValue(
            method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"),
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/json/ModelTransformation;getTransformation(Lnet/minecraft/client/render/model/json/ModelTransformationMode;)Lnet/minecraft/client/render/model/json/Transformation;"))
    )
    public boolean a(boolean isTrident, @Local ItemStack stack) {
        return isTrident || stack.isOf(ZauberItems.STAFF);
    }*/
}
