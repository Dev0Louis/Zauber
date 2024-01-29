package dev.louis.zauber.spell;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellType;
import dev.louis.zauber.config.ZauberConfig;
import net.minecraft.block.Fertilizable;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldEvents;

public class SproutSpell extends Spell {
    public int grownCrops = 0;
    public SproutSpell(SpellType<?> spellType) {
        super(spellType);
    }

    @Override
    public void cast() {

    }

    @Override
    public void tick() {
        if(getCaster() instanceof ServerPlayerEntity serverPlayer) {
            var box = serverPlayer.getBoundingBox().expand(5, 3, 5);
            BlockPos.stream(box).forEach(pos -> {

                if(!serverPlayer.getManaManager().hasEnoughMana(1)) {
                    stop();
                    return;
                }

                var world = serverPlayer.getServerWorld();
                if(world.random.nextInt(200) != 0)return;
                var blockState = serverPlayer.getServerWorld().getBlockState(pos);
                if (blockState.getBlock() instanceof Fertilizable fertilizable && fertilizable.isFertilizable(world, pos, blockState)) {
                    if (fertilizable.canGrow(world, world.random, pos, blockState)) {
                        fertilizable.grow(world, world.random, pos, blockState);
                        world.syncWorldEvent(WorldEvents.BONE_MEAL_USED, pos, 0);
                        grownCrops++;
                        if(grownCrops % 10 == 0) {
                            serverPlayer.getManaManager().drainMana(1);
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getDuration() {
        return ZauberConfig.getSproutSpellDuration();
    }
}
