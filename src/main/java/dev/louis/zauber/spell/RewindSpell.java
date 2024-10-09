package dev.louis.zauber.spell;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.zauber.spell.effect.RewindSpellEffect;
import dev.louis.zauber.spell.type.SpellType;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.TeleportTarget;

public class RewindSpell extends ZauberSpell<LivingEntity> {
    private static final float COST = 3;

    public RewindSpell() {
        super(SpellType.REWIND);
    }

    @Override
    public void cast(SpellSource<LivingEntity> source) {
        if (source.getWorld().isClient()) return;
        source.getManaPool().ifPresent(manaPool -> {
            try (Transaction t1 = Transaction.openOuter()) {
                if (manaPool.extractMana(COST, t1) < COST) return;

                RewindSpellEffect spellEffect = new RewindSpellEffect(source.getCaster());
                source.getCaster().startSpellEffect(spellEffect);
                t1.commit();
            }
        });
    }
}
