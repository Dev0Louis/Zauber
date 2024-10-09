package dev.louis.zauber.spell.effect;

import dev.louis.nebula.api.spell.SpellEffect;
import dev.louis.zauber.config.ConfigManager;
import dev.louis.zauber.spell.effect.type.SpellEffectTypes;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Fertilizable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.WorldEvents;

public class SproutSpellEffect extends SpellEffect {
    public static final float MANA_COST_PER_CROP = 0.1f;
    public boolean manaExtractionFail;


    public SproutSpellEffect(LivingEntity target) {
        super(SpellEffectTypes.SPROUT, target);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void tick() {
        if (!target.getWorld().isClient()) {
            var box = target.getBoundingBox().expand(5, 3, 5);
            BlockPos.stream(box).forEach(pos -> {



                var world = target.getWorld();
                if (target.getRandom().nextInt(200) != 0) return;
                var blockState = world.getBlockState(pos);
                if (blockState.getBlock() instanceof Fertilizable fertilizable && fertilizable.isFertilizable(world, pos, blockState)) {
                    if (fertilizable.canGrow(world, world.random, pos, blockState)) {
                        fertilizable.grow((ServerWorld) world, world.random, pos, blockState);
                        world.syncWorldEvent(WorldEvents.BONE_MEAL_USED, pos, 0);
                        try(Transaction t1 = Transaction.openOuter()) {
                            var mana = target.getManaManager().extractMana(MANA_COST_PER_CROP, t1);
                            if (mana == MANA_COST_PER_CROP) t1.commit();
                            else manaExtractionFail = true;

                        }
                    }

                }
            });
        }

    }


    @Override
    public void onEnd() {

    }

    @Override
    public boolean shouldContinue() {
        return age < ConfigManager.getServerConfig().sproutSpellDuration();
    }
}
