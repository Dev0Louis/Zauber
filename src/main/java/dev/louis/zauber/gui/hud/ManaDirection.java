package dev.louis.zauber.gui.hud;

import net.minecraft.util.StringIdentifiable;

public enum ManaDirection implements StringIdentifiable {
    LEFT,
    RIGHT;

    //public static final StringIdentifiable.EnumCodec<ManaDirection> CODEC = StringIdentifiable.createCodec(ManaDirection::values);

    @Override
    public String asString() {
        return "zauber.mana_direction." + this.asString().toLowerCase();
    }
}