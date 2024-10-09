package dev.louis.zauber.spell;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.zauber.spell.type.SpellType;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

public class PushSpell extends EntitiyTargetingSpell {
    public PushSpell() {
        super(SpellType.PUSH);
    }

    @Override
    public void cast(SpellSource<LivingEntity> source) {
        source.getManaPool().ifPresent(manaPool -> {
            getTargetedEntity(source).ifPresent(pulled -> {
                try(Transaction t1 = Transaction.openOuter()) {
                    var extraction = manaPool.extractMana(2, t1);
                    if (extraction < 2) return;

                    Vec3d velocity = source.getPos().subtract(pulled.getPos()).normalize().negate();
                    pulled.setVelocity(velocity);
                    pulled.velocityModified = true;
                    t1.commit();
                }
            });
        });
    }
}
