package dev.louis.zauber.mana.effect;

import dev.louis.nebula.api.mana.pool.ManaPoolHolder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class ManaRegenerationStatusEffect extends StatusEffect {
    public ManaRegenerationStatusEffect() {
        super(StatusEffectCategory.BENEFICIAL,
                0x7612ff);
    }


    // This method is called every deletionTick to check whether it should apply the status effect or not
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // In our case, we just make it return true so that it applies the status effect every deletionTick.
        return true;
    }

    // This method is called when it applies the status effect. We implement custom functionality here.
    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (!entity.getWorld().isClient() && entity instanceof ManaPoolHolder manaPoolHolder) {
            var pool = manaPoolHolder.getManaPool();
            pool.insertMana(1 + amplifier);
        }
        return true;
    }

}
