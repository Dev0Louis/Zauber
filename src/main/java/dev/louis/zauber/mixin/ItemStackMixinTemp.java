package dev.louis.zauber.mixin;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public class ItemStackMixinTemp {
    @Inject(
            method = "method_57377",
            at = @At("RETURN")
    )
    private static void a(String error, CallbackInfo ci) {
        new Exception().printStackTrace();
    }
}
