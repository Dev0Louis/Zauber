package dev.louis.zauber.spell;

import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.zauber.spell.type.SpellType;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

public class PullSpell extends EntitiyTargetingSpell {
    public PullSpell() {
        super(SpellType.PULL);
    }

    @Override
    public void cast(SpellSource<LivingEntity> source) {
        source.getManaPool().ifPresent(manaPool -> {
            getTargetedEntity(source).ifPresent(pulled -> {
                try(Transaction t1 = Transaction.openOuter()) {
                    var extraction = manaPool.extractMana(2, t1);
                    if (extraction < 2) return;

                    Vec3d velocity = source.getPos().subtract(pulled.getPos()).normalize();
                    pulled.setVelocity(velocity);
                    pulled.velocityModified = true;
                    t1.commit();
                }
            });
        });
    }
}
