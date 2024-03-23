package dev.louis.zauber.spell;


import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellType;
import net.minecraft.entity.player.PlayerEntity;

public class SuicideSpell extends Spell {
    public SuicideSpell(SpellType<?> spellType, PlayerEntity caster) {
        super(spellType, caster);
    }

    @Override
    public void cast() {
        this.getCaster().kill();
    }
}
