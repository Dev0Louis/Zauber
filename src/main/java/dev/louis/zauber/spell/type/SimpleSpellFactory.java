package dev.louis.zauber.spell.type;

import dev.louis.nebula.api.spell.Spell;

public non-sealed interface SimpleSpellFactory<C, S extends Spell<C>> extends SpellFactory<C, S> {
    S create();

}
