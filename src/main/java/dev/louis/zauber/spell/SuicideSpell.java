package dev.louis.zauber.spell;


import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.nebula.api.spell.quick.SpellException;
import net.minecraft.entity.LivingEntity;

public class SuicideSpell implements Spell<LivingEntity> {

    @Override
    public void cast(SpellSource<LivingEntity> spellSource) throws SpellException {
        spellSource.getCaster().kill();
    }
}
