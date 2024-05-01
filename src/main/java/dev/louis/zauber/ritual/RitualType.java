package dev.louis.zauber.ritual;

public record RitualType<R extends Ritual>(Ritual.Starter starter) {
    public static final RitualType<?> MANA_HORSE = new RitualType<>(HorseRitual::tryStart);
}
