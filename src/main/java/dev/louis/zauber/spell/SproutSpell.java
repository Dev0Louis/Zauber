package dev.louis.zauber.spell;

import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.nebula.api.spell.quick.SpellException;
import dev.louis.zauber.spell.effect.SproutSpellEffect;
import dev.louis.zauber.spell.type.SpellType;
import net.minecraft.entity.LivingEntity;

public class SproutSpell extends ZauberSpell<LivingEntity> {

    public SproutSpell(SpellType<?> type) {
        super(type);
    }

    @Override
    public void cast(SpellSource<LivingEntity> source) throws SpellException {
        var manaPool = source.getManaPool().orElseThrow(SpellException::new);
        var hasEnoughMana = manaPool.getMana() > SproutSpellEffect.MANA_COST_PER_CROP;
        if (hasEnoughMana) {
            SproutSpellEffect spellEffect = new SproutSpellEffect(source.getCaster());
            var startSpellEffect = source.getCaster().startSpellEffect(spellEffect);
            if (!startSpellEffect) throw new SpellException();
        }

    }
}
