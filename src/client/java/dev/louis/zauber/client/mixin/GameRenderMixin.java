package dev.louis.zauber.client.mixin;

import dev.louis.zauber.client.screen.RippedPageScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRenderMixin {
    @Inject(
            method = "onResized",
            at = @At("HEAD")
    )
    public void resizeStencilBuffer(int width, int height, CallbackInfo ci) {
        if (RippedPageScreen.stencilFrameBuffer == null) return;
        RippedPageScreen.stencilFrameBuffer.resize(width, height, MinecraftClient.IS_SYSTEM_MAC);
    }
}
