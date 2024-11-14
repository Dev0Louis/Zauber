package dev.louis.zauber.client.telekinesis;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.louis.zauber.Zauber;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

public class TelekinesisPad {
    public static final Identifier TEXTURE = Identifier.of(Zauber.MOD_ID, "telekinesis_indicator");


    public static void render(DrawContext context, RenderTickCounter tickCounter, float progress) {
        context.getMatrices().push();
        int a = (int) (64 * progress);
        RenderSystem.setShaderColor(0, 0, 1, (float) Math.min(1, 1.25 - progress));
        RenderSystem.enableBlend();
        context.getMatrices().translate(-a / 2f, -a / 2f, 0);
        context.drawGuiTexture(TEXTURE, context.getScaledWindowWidth() / 2, context.getScaledWindowHeight() / 2, a, a);

        RenderSystem.setShaderColor(1,1,1,1);
        RenderSystem.defaultBlendFunc();
        context.getMatrices().pop();

        context.drawText(
                MinecraftClient.getInstance().textRenderer,
                String.valueOf(MinecraftClient.getInstance().options.useKey.isPressed()),
                0,
                0,
                0xFFFFFF,
                true
        );
    }
}
