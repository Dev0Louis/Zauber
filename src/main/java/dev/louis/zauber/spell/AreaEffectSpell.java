package dev.louis.zauber.spell;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.nebula.api.spell.quick.SpellException;
import dev.louis.zauber.entity.AreaSpellEffectEntity;
import dev.louis.zauber.spell.type.SpellType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

import static java.lang.Math.*;

public abstract class AreaEffectSpell extends ZauberSpell<LivingEntity> {
    private final AreaSpellEffectEntity.Type type;

    public AreaEffectSpell(
            SpellType<? extends AreaEffectSpell> spellType,
            AreaSpellEffectEntity.Type type
    ) {
        super(spellType);
        this.type = type;
    }

    @Override
    public void cast(SpellSource<LivingEntity> source) throws SpellException {
        var manaPool = source.getManaPool().orElseThrow(SpellException::new);
        Spell.drainMana(manaPool, 4);
        var pos = AreaEffectSpell.getSpellCastingPos(source.getCaster());
        AreaSpellEffectEntity areaSpellEffectEntity = new AreaSpellEffectEntity(source.getWorld(), type);
        areaSpellEffectEntity.setPosition(pos);
        source.getWorld().spawnEntity(areaSpellEffectEntity);
    }

    private static Vec3d getSpellCastingPos(LivingEntity entity) {
        Vec3d playerRotation = entity.getRotationVec(1.0f).normalize();
        double x = playerRotation.x;
        double z = playerRotation.z;

        double absX = abs(x);
        double absZ = abs(z);

        final double threshold = 0.4;
        final double minLockRotation = 0.35;

        if (absX + absZ < threshold) {
            x = copySign(max(absX, minLockRotation), x);
            z = copySign(max(absZ, minLockRotation), z);
        }
        Vec3d adjustedRotation = new Vec3d(x, 0, z);
        final double multiplier = 3;
        final double yOffset = -0.35;

        return entity.getPos().add(adjustedRotation.multiply(multiplier)).add(0, yOffset, 0);
    }
}
