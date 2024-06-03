package dev.louis.zauber.spell;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;

public abstract class EntitiyTargetingSpell extends Spell {
    private Entity entity;

    public EntitiyTargetingSpell(SpellType<?> spellType, PlayerEntity caster) {
        super(spellType, caster);
        if (caster.raycast(24, 0, false) instanceof EntityHitResult entityHitResult) entity = entityHitResult.getEntity();
    }


    @Override
    public void cast() {

    }

    //Is not null in cast.
    public Entity castedOn() {
        return entity;
    }

    @Override
    public boolean isCastable() {
        return castedOn() != null;
    }
}
