package dev.louis.zauber.spell;

import dev.louis.nebula.api.spell.SpellType;
import dev.louis.zauber.config.ZauberConfig;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;

public class FireSpell extends AreaEffectSpell {
    public FireSpell(SpellType<? extends AreaEffectSpell> spellType) {
        super(spellType, ParticleTypes.FLAME, ZauberConfig.getFireSpellDuration());
    }

    @Override
    protected void affect(Entity entity) {
        entity.setVelocity(entity.getPos().subtract(getCaster().getPos()).normalize().add(0, 1, 0));
        entity.velocityModified = true;
        entity.setFireTicks(100);
        super.affect(entity);
    }
}
