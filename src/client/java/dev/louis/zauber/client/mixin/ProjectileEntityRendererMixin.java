package dev.louis.zauber.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.louis.zauber.client.render.entity.BlueArrowEntityRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ProjectileEntityRenderer.class)
public class ProjectileEntityRendererMixin {
    @SuppressWarnings("UnreachableCode")
    @WrapOperation(
            method = "render(Lnet/minecraft/entity/projectile/PersistentProjectileEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getEntityCutout(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;")
    )
    public RenderLayer makeBlueArrowEntityRendererChooseOwnRenderlayer(Identifier texture, Operation<RenderLayer> original) {
        if (((Object) this) instanceof BlueArrowEntityRenderer entityRenderer) return entityRenderer.getRenderLayer(texture);
        return original.call(texture);
    }
}
