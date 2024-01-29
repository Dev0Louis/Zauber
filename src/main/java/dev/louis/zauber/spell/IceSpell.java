package dev.louis.zauber.spell;

import dev.louis.nebula.api.spell.SpellType;
import dev.louis.zauber.config.ZauberConfig;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;

;

public class IceSpell extends AreaEffectSpell {
    public IceSpell(SpellType<? extends AreaEffectSpell> spellType) {
        super(spellType, ParticleTypes.SNOWFLAKE, ZauberConfig.getIceSpellDuration());
    }

    @Override
    protected void affect(Entity entity) {
        entity.setVelocity(Vec3d.ZERO);
        entity.velocityModified = true;
        entity.setFrozenTicks(100);
        entity.extinguishWithSound();
        super.affect(entity);
    }
}
