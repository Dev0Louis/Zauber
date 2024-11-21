package dev.louis.zauber.mana.effect;

import dev.louis.nebula.api.mana.pool.ManaPoolHolder;
import dev.louis.zauber.Zauber;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.InstantStatusEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

public class InstantManaStatusEffect extends InstantStatusEffect {
    public InstantManaStatusEffect() {
        super(StatusEffectCategory.BENEFICIAL,
                0x5900e2);
    }

    @Override
    public void applyInstantEffect(ServerWorld world, @Nullable Entity effectEntity, @Nullable Entity attacker, LivingEntity target, int amplifier, double proximity) {
        if (target instanceof ManaPoolHolder manaPoolHolder) {
            manaPoolHolder.getManaPool().insertMana((amplifier + 1) * 5);
        }
    }

}
