package dev.louis.zauber.spell;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.zauber.spell.type.SpellType;

public abstract class ZauberSpell<Caster> implements Spell<Caster> {
    private final SpellType<?, ?> type;

    public ZauberSpell(SpellType<?, ?> type) {
        this.type = type;
    }

    public SpellType<?, ?> getType() {
        return type;
    }
}
