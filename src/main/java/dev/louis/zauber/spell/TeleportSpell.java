package dev.louis.zauber.spell;

import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.zauber.spell.type.SpellType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.sound.SoundEvents;

public class TeleportSpell extends EntitiyTargetingSpell {

    public TeleportSpell() {
        super(SpellType.TELEPORT);
    }

    @Override
    public void cast(SpellSource<LivingEntity> source) {
        this.getTargetedEntity(source).ifPresent(target -> {
            double x = target.getX();
            double y = target.getY();
            double z = target.getZ();
            source.getCaster().playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1, 1);

            if (!source.getWorld().isClient()) {
                source.getCaster().teleport(x, y, z, true);
            }
        });
    }
}
