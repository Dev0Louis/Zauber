package dev.louis.zauber.spell.type;

import dev.louis.nebula.api.spell.Spell;
import net.minecraft.entity.LivingEntity;

public non-sealed interface PlayerSpellFactory<S extends Spell<LivingEntity>> extends SpellFactory<S> {
    S create();

}
