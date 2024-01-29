package dev.louis.zauber.gui.hud;

import dev.louis.zauber.Zauber;
import dev.louis.zauber.config.ZauberConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class ManaDrawer {
    public static void renderMana(Mana mana, DrawContext context, int x, int y) {
        switch (mana) {
            case FULL -> render(new Identifier(Zauber.MOD_ID,"textures/gui/mana.png"), context, x, y);
            case HALF -> render(new Identifier(Zauber.MOD_ID,"textures/gui/half_mana.png"), context, x, y);
            case EMPTY -> render(new Identifier(Zauber.MOD_ID,"textures/gui/empty_mana.png"), context, x, y);
        }
    }

    private static void render(Identifier texture, DrawContext context, int x, int y) {
        context.getMatrices().push();
        boolean invert = ZauberConfig.getManaDirection() == ManaDirection.LEFT;

        context.drawTexture(texture, x, y, 9, 9, 9, 9, invert ? -9 : 9, 9);
        context.getMatrices().pop();
    }

    public enum Mana{
        FULL,
        HALF,
        EMPTY
    }
}
