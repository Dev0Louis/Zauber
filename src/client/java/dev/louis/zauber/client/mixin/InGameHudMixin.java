package dev.louis.zauber.client.mixin;

import dev.louis.zauber.client.ManaDrawer;
import dev.louis.zauber.config.ConfigManager;
import dev.louis.zauber.mana.ManaDirection;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.FluidTags;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow protected abstract PlayerEntity getCameraPlayer();

    @Shadow @Final private MinecraftClient client;

    @Inject(at = @At("RETURN"), method = "renderStatusBars")
    public void renderStatusBar(DrawContext context, CallbackInfo ci){
        var playerEntity = this.getCameraPlayer();
        int mana = client.player.getManaManager().getMana();
        if(isInWater(playerEntity) && mana <= 0) {
            return;
        }
        var scaledWidth = context.getScaledWindowWidth();
        var scaledHeight = context.getScaledWindowHeight();
        int mid = scaledWidth / 2 + 18;
        int n = scaledWidth / 2 + 91;

        int x;
        int y = (scaledHeight - 39 - 10);

        for(int w = 0; w < 10; ++w) {
            x = calculatePosition(mid, n, w);
            ManaDrawer.renderMana(ManaDrawer.RenderType.EMPTY, context, x, y);

            if((w * 2 + 1 < mana)) {
                ManaDrawer.renderMana(ManaDrawer.RenderType.FULL, context, x, y);
            }

            if (w * 2 + 1 == mana) {
                ManaDrawer.renderMana(ManaDrawer.RenderType.HALF, context, x, y);
            }

        }
    }

    private int calculatePosition(int mid, int n, int w) {
        if(ConfigManager.getClientConfig().manaDirection() == ManaDirection.RIGHT) {
            return mid + w * 8 - 9;
        }else {
            return n - w * 8 - 9;
        }
    }

    private boolean isInWater(PlayerEntity playerEntity) {

        int y = playerEntity.getMaxAir();
        int z = Math.min(playerEntity.getAir(), y);

        return playerEntity.isSubmergedIn(FluidTags.WATER) || z < y;
    }

    @ModifyArg(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;getHeartRows(I)I"))
    public int modifyVariable(int heartCount) {
        if(!(MinecraftClient.getInstance().player.getManaManager().getMana()<=0)) {
            heartCount = heartCount + 10;
        }
        return heartCount;
    }

}
