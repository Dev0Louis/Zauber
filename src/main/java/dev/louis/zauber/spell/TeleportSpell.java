package dev.louis.zauber.spell;

import dev.louis.nebula.api.spell.SpellType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

public class TeleportSpell extends TargetingSpell {
    public TeleportSpell(SpellType<?> spellType, PlayerEntity caster) {
        super(spellType, caster);
    }

    @Override
    public void cast() {
        double x = castedOn().getX();
        double y = castedOn().getY();
        double z = castedOn().getZ();
        getCaster().getWorld().playSound(null, BlockPos.ofFloored(getCaster().getX(),getCaster().getY(),getCaster().getZ()), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1, 1);
        getCaster().teleport(x, y, z, true);
        getCaster().getWorld().playSound(null, BlockPos.ofFloored(x,y,z), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1, 1);
    }
}
