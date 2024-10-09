package dev.louis.zauber.spell;

import dev.louis.zauber.entity.AreaSpellEffectEntity;
import dev.louis.zauber.spell.type.SpellType;

public class FireSpell extends AreaEffectSpell {
    public FireSpell() {
        super(SpellType.FIRE, AreaSpellEffectEntity.Type.FIRE);
    }
}
