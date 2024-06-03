package dev.louis.zauber.spell;

import dev.louis.nebula.api.spell.SpellType;
import dev.louis.zauber.entity.HailStoneEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HailStoneSpell extends BlockTargetingSpell {
    public HailStoneSpell(SpellType<?> spellType, PlayerEntity caster) {
        super(spellType, caster);
    }

    @Override
    public void cast() {
        World world = caster.getWorld();
        BlockPos pos = this.pos().up(3);
        final int size = 3;
        for (int x = -size; x < size; x++) {
            for (int y = -size; y < size; y++) {
                HailStoneEntity.TYPE.spawn((ServerWorld) world, pos.add(x, 0, y), SpawnReason.NATURAL);
            }
        }

    }
}
