package dev.louis.zauber.spell.type;

import dev.louis.nebula.api.spell.Spell;

public sealed interface SpellFactory<C, S extends Spell<C>> permits SimpleSpellFactory {

}
