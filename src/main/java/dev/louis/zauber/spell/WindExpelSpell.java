package dev.louis.zauber.spell;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.nebula.api.spell.quick.SpellException;
import dev.louis.zauber.spell.effect.CloudJumpSpellEffect;
import dev.louis.zauber.spell.type.SpellType;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.entity.LivingEntity;

public class WindExpelSpell extends ZauberSpell<LivingEntity> {
    public WindExpelSpell() {
        super(SpellType.WIND_EXPEL);
    }

    @Override
    public void cast(SpellSource<LivingEntity> source) throws SpellException {
        var manaPool = source.getManaPool().orElseThrow(SpellException::create);
        try(Transaction transaction = Transaction.openOuter()) {
            Spell.drainMana(manaPool, 1, transaction);
            var startedSpellEffect = source.getCaster().startSpellEffect(new CloudJumpSpellEffect(source.getCaster()));
            if (!startedSpellEffect) throw new SpellException();
            transaction.commit();
        }
    }
}
