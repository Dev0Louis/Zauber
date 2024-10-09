package dev.louis.zauber.spell.type;

import dev.louis.nebula.api.spell.Spell;

public non-sealed interface SimpleSpellFactory<S extends Spell<?>> extends SpellFactory<S> {
    S create();

}
