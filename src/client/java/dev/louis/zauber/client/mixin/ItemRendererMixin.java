package dev.louis.zauber.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.louis.zauber.client.render.StaffItemRenderer;
import dev.louis.zauber.item.ZauberItems;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

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

    /*@ModifyExpressionValue(
            method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"),
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/json/ModelTransformation;getTransformation(Lnet/minecraft/client/render/model/json/ModelTransformationMode;)Lnet/minecraft/client/render/model/json/Transformation;"))
    )
    public boolean a(boolean isTrident, @Local ItemStack stack) {
        return isTrident || stack.isOf(ZauberItems.STAFF);
    }*/
}
