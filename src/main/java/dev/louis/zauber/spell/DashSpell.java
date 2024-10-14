package dev.louis.zauber.spell;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.nebula.api.spell.quick.SpellException;
import dev.louis.zauber.spell.effect.DashSpellEffect;
import dev.louis.zauber.spell.type.SpellType;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.entity.LivingEntity;

public class DashSpell extends ZauberSpell<LivingEntity> {

    public DashSpell() {
        super(SpellType.DASH);
    }

    @Override
    public void cast(SpellSource<LivingEntity> source) throws SpellException {
        var manaPool = source.getManaPool().orElseThrow(SpellException::new);
        try (Transaction t1 = Transaction.openOuter()) {
            Spell.drainMana(manaPool, 2, t1);
            DashSpellEffect spellEffect = new DashSpellEffect(source.getCaster());
            var startSpellEffect = source.getCaster().startSpellEffect(spellEffect);
            if (!startSpellEffect) throw new SpellException();
            t1.commit();
        }
    }
}
