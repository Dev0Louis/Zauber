package dev.louis.zauber.config;


import dev.louis.zauber.mana.ManaDirection;

import java.awt.*;

/**
 * Not meant to be used on the server, but it is common code.
 * Maybe send it to server in the future?
 */
public class ClientConfig {
    private ManaDirection manaDirection = ManaDirection.RIGHT;
    public int targetingColor = Color.RED.getRGB();

    public ManaDirection manaDirection() {
        return manaDirection;
    }

    public void manaDirection(ManaDirection manaDirection) {
        this.manaDirection = manaDirection;
    }

    public Color targetingColor() {
        return new Color(targetingColor);
    }

    public void targetingColor(Color targetingColor) {
        this.targetingColor = targetingColor.getRGB();
    }
}
