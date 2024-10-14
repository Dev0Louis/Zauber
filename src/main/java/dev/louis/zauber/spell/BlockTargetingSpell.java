package dev.louis.zauber.spell;

import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.zauber.spell.type.SpellType;

import dev.louis.nebula.api.spell.quick.SpellException;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

public abstract class BlockTargetingSpell extends ZauberSpell<LivingEntity> {

    public BlockTargetingSpell(SpellType<? extends BlockTargetingSpell> spellType) {
        super(spellType);
    }

    public BlockHitResult raycastOrThrow(SpellSource<LivingEntity> source) throws SpellException {
        var caster = source.getCaster();
        var hitResult = caster.raycast(1/*ConfigManager.getServerConfig().blockTargetingDistance()*/, 0, false);
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            return (BlockHitResult) hitResult;
        }
        throw new SpellException();
    }

}
