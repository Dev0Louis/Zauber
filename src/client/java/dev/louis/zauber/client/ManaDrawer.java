package dev.louis.zauber.client;

import dev.louis.zauber.Zauber;
import dev.louis.zauber.config.ConfigManager;
import dev.louis.zauber.mana.ManaDirection;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class ManaDrawer {
    public static void renderMana(RenderType manaRenderType, DrawContext context, int x, int y) {
        switch (manaRenderType) {
            case FULL -> render(new Identifier(Zauber.MOD_ID,"textures/gui/mana.png"), context, x, y);
            case HALF -> render(new Identifier(Zauber.MOD_ID,"textures/gui/half_mana.png"), context, x, y);
            case EMPTY -> render(new Identifier(Zauber.MOD_ID,"textures/gui/empty_mana.png"), context, x, y);
        }
    }

    private static void render(Identifier texture, DrawContext context, int x, int y) {
        context.getMatrices().push();
        boolean invert = ConfigManager.getClientConfig().manaDirection() == ManaDirection.LEFT;

        context.drawTexture(texture, x, y, 9, 9, 9, 9, invert ? -9 : 9, 9);
        context.getMatrices().pop();
    }

    public enum RenderType {
        FULL,
        HALF,
        EMPTY
    }
}
