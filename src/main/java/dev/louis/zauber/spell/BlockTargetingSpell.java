package dev.louis.zauber.spell;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellType;
import dev.louis.zauber.config.ConfigManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public abstract class BlockTargetingSpell extends Spell {
    protected final BlockPos pos;

    public BlockTargetingSpell(SpellType<?> spellType, PlayerEntity caster) {
        super(spellType, caster);
        var hitResult = caster.raycast(ConfigManager.getServerConfig().blockTargetingDistance(), 0, false);
        if (hitResult.getType() == HitResult.Type.BLOCK)
            pos = ((BlockHitResult) hitResult).getBlockPos().offset(((BlockHitResult) hitResult).getSide());
            //Don't annoy me java!
        else pos = null;
    }


    @Override
    public void cast() {

    }

    //Is not null in cast.
    public BlockPos pos() {
        return pos;
    }

    @Override
    public boolean isCastable() {
        return pos() != null && super.isCastable();
    }
}
