package dev.louis.zauber.mana;

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