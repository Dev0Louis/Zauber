package dev.louis.zauber.spell;


import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellType;

public class SuicideSpell extends Spell {
    public SuicideSpell(SpellType<? extends Spell> spellType) {
        super(spellType);
    }

    @Override
    public void cast() {
        this.getCaster().kill();
    }
}
