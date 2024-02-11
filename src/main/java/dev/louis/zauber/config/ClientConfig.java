package dev.louis.zauber.config;

import dev.louis.zauber.gui.hud.ManaDirection;

import java.awt.*;

public class ClientConfig {
    private ManaDirection manaDirection = ManaDirection.RIGHT;

    private int raycastScanPrecision = 2;

    public int targetingColor = Color.RED.getRGB();

    public ManaDirection manaDirection() {
        return manaDirection;
    }

    public void manaDirection(ManaDirection manaDirection) {
        this.manaDirection = manaDirection;
    }

    public int raycastScanPrecision() {
        return raycastScanPrecision;
    }

    public void raycastScanPrecision(int raycastScanPrecision) {
        this.raycastScanPrecision = raycastScanPrecision;
    }

    public Color targetingColor() {
        return new Color(targetingColor);
    }

    public void targetingColor(Color targetingColor) {
        this.targetingColor = targetingColor.getRGB();
    }
}
