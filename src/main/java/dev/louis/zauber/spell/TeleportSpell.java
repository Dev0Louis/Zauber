package dev.louis.zauber.spell;

import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.nebula.api.spell.SpellType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

public class TeleportSpell extends EntitiyTargetingSpell {

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
