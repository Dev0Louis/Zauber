package dev.louis.zauber.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.louis.nebula.api.NebulaPlayer;
import dev.louis.zauber.Zauber;
import dev.louis.zauber.spell.VengeanceSpell;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @ModifyReturnValue(
            method = "isPushable", at = @At("RETURN"))
    public boolean dashingPlayersAreNotPushable(boolean original) {
        return original || ((Object)this instanceof PlayerEntity player && player.getSpellManager().isSpellTypeActive(Zauber.Spells.DASH));
    }

    @Inject(
            method = "applyDamage",
            at = @At("HEAD"),
            cancellable = true
    )
    public void storedAppliedDamageForLater(DamageSource source, float amount, CallbackInfo ci) {
        if (this instanceof NebulaPlayer nebulaPlayer) {
            var activeSpells = nebulaPlayer.getSpellManager().getActiveSpells();
            var refusalOfDeathSpells = activeSpells.stream().filter(spell -> spell instanceof VengeanceSpell).map(spell -> (VengeanceSpell) spell).toList();
            if (!refusalOfDeathSpells.isEmpty()) {
                refusalOfDeathSpells.forEach(vengeanceSpell -> vengeanceSpell.onDamage(source, amount));
                ci.cancel();
            }

        }
    }
}
