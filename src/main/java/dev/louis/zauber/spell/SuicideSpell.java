package dev.louis.zauber.spell;


import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.nebula.api.spell.quick.SpellException;
import dev.louis.zauber.spell.type.SpellType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

public class SuicideSpell extends ZauberSpell<LivingEntity> {

    public SuicideSpell() {
        super(SpellType.SUICIDE);
    }

    @Override
    public void cast(SpellSource<LivingEntity> spellSource) throws SpellException {
        spellSource.getCaster().kill((ServerWorld) spellSource.getWorld());
    }
}
