package dev.louis.zauber.spell.type;

import dev.louis.nebula.api.spell.Spell;

public sealed interface SpellFactory<S extends Spell<?>> permits PlayerSpellFactory {

}
