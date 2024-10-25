package dev.louis.zauber.client.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.louis.zauber.extension.EntityExtension;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerMixin {
    @WrapWithCondition(
            method = "tickMovement",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/Input;tick(ZF)V")
    )
    public boolean a(Input instance, boolean slowDown, float slowDownFactor) {
        return !((EntityExtension) this).zauber$isTelekinesed();
    }
}
