package dev.louis.zauber.spell;

import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.nebula.api.spell.quick.SpellException;
import dev.louis.zauber.spell.effect.JuggernautSpellEffect;
import dev.louis.zauber.spell.type.SpellType;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.entity.LivingEntity;

public class JuggernautSpell extends ZauberSpell<LivingEntity> {

    public JuggernautSpell() {
        super(SpellType.JUGGERNAUT);
    }

    @Override
    public void cast(SpellSource<LivingEntity> source) throws SpellException {
        var manaPool = source.getManaPool().orElseThrow(SpellException::new);
        try(Transaction t1 = Transaction.openOuter()) {
            var capacity = manaPool.getCapacity();
            var extracted = manaPool.extractMana(capacity, t1);

            if (extracted < capacity) return;
            if (source.getCaster().startSpellEffect(new JuggernautSpellEffect(source.getCaster()))) {
                t1.commit();
            }
        }
    }
}
