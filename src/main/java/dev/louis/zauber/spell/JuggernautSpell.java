package dev.louis.zauber.spell;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.zauber.spell.effect.JuggernautSpellEffect;
import dev.louis.zauber.spell.type.SpellType;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.entity.player.PlayerEntity;

public class JuggernautSpell extends ZauberSpell<PlayerEntity> {

    public JuggernautSpell() {
        super(SpellType.JUGGERNAUT);
    }

    @Override
    public void cast(SpellSource<PlayerEntity> source) {
        source.getManaPool().ifPresent(manaPool -> {
            try(Transaction t1 = Transaction.openOuter()) {
                var capacity = manaPool.getCapacity();
                var extracted = manaPool.extractMana(capacity, t1);

                if (extracted < capacity) return;
                if (source.getCaster().startSpellEffect(new JuggernautSpellEffect())) {
                    t1.commit();
                }
            }


        });
    }
}
