package dev.louis.zauber.spell;

import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.nebula.api.spell.quick.SpellException;
import dev.louis.zauber.entity.HailStormEntity;
import dev.louis.zauber.spell.type.SpellType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;

public class HailStormSpell extends BlockTargetingSpell {
    public HailStormSpell() {
        super(SpellType.HAIL_STORM);
    }

    @Override
    public void cast(SpellSource<LivingEntity> source) throws SpellException {
        var blockHit = raycastOrThrow(source);
        var manaPool = source.getManaPool().orElseThrow(SpellException::new);

        var upPos = blockHit.getBlockPos().up(30).toCenterPos();

        var entity = new HailStormEntity(source.getWorld(), source.getCaster());
        entity.setPosition(upPos);

        source.getWorld().spawnEntity(entity);
    }

}
