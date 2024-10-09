package dev.louis.zauber.spell;

import dev.louis.zauber.entity.AreaSpellEffectEntity;
import dev.louis.zauber.spell.type.SpellType;

public class IceSpell extends AreaEffectSpell {
    public IceSpell() {
        super(SpellType.ICE, AreaSpellEffectEntity.Type.ICE);
    }
}
