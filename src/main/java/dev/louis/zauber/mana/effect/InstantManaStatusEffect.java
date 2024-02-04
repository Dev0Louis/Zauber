package dev.louis.zauber.mana.effect;

import dev.louis.zauber.Zauber;
import dev.louis.nebula.api.NebulaPlayer;
import eu.pb4.polymer.core.api.other.PolymerStatusEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.InstantStatusEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public class InstantManaStatusEffect extends InstantStatusEffect implements PolymerStatusEffect {
    public InstantManaStatusEffect() {
        super(StatusEffectCategory.BENEFICIAL,
                0x03ddfe);
    }

    @Override
    public void applyInstantEffect(@Nullable Entity source, @Nullable Entity attacker, LivingEntity target, int amplifier, double proximity) {
        if (target instanceof NebulaPlayer nebulaPlayer) {
            nebulaPlayer.getManaManager().addMana((amplifier+1)*5);
        }
    }
    @Override
    public StatusEffect getPolymerReplacement(ServerPlayerEntity player) {
        if(Zauber.isClientModded(player))return this;
        return null;
    }
}
