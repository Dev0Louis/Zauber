package dev.louis.zauber.mana.effect;

import dev.louis.zauber.Zauber;
import dev.louis.nebula.api.manager.mana.ManaManager;
import eu.pb4.polymer.core.api.other.PolymerStatusEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class ManaRegenerationStatusEffect extends StatusEffect implements PolymerStatusEffect {
    public ManaRegenerationStatusEffect() {
        super(StatusEffectCategory.BENEFICIAL,
                0x98D982);
    }


    // This method is called every tick to check whether it should apply the status effect or not
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // In our case, we just make it return true so that it applies the status effect every tick.
        return true;
    }

    // This method is called when it applies the status effect. We implement custom functionality here.
    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (!entity.getWorld().isClient() && entity instanceof PlayerEntity player) {
            ManaManager playerManaManager = player.getManaManager();
            playerManaManager.addMana(1+amplifier);
        }
    }

    @Override
    public StatusEffect getPolymerReplacement(ServerPlayerEntity player) {
        if(Zauber.isClientVanilla(player))return null;
        return this;
    }
}
