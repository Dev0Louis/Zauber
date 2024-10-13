package dev.louis.zauber.client.mixin;

import dev.louis.zauber.client.ZauberClient;
import dev.louis.zauber.client.model.StaffItemModel;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.resource.ResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BuiltinModelItemRenderer.class)
public class BuiltinModelItemRendererMixin {
    @Shadow @Final private EntityModelLoader entityModelLoader;
    StaffItemModel modelStaff;

    @Inject(
            method = "reload",
            at = @At("HEAD")
    )
    public void a(ResourceManager manager, CallbackInfo ci) {
        modelStaff = new StaffItemModel(this.entityModelLoader.getModelPart(ZauberClient.STAFF_MODEL_LAYER));
    }
}
