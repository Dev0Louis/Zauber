package dev.louis.zauber.mixin;

import dev.louis.nebula.api.NebulaPlayer;
import dev.louis.zauber.Zauber;
import dev.louis.zauber.item.HeartOfTheDarknessItem;
import dev.louis.zauber.item.ZauberItems;
import dev.louis.zauber.spell.VengeanceSpell;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("UnreachableCode")
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    public void makeHeartDisappearInLight(CallbackInfo ci) {
        if (!this.getWorld().isClient) {
            if (this.getMainHandStack().isOf(ZauberItems.HEART_OF_THE_DARKNESS) && this.getWorld().getLightLevel(this.getBlockPos()) > HeartOfTheDarknessItem.MAX_BRIGHTNESS) {
                HeartOfTheDarknessItem.onDisappeared((ServerWorld) this.getWorld(), this.getEyePos());
                this.getMainHandStack().decrement(1);
            }
            if (age % 20 == 0) {
                if (Zauber.isInTrappingBed((PlayerEntity) (Object) this)) {
                    this.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 40));
                }
            }
        }
    }

    @Override
    public void applyDamage(DamageSource source, float amount) {
        var activeSpells = ((NebulaPlayer) this).getSpellManager().getActiveSpells();
        var refusalOfDeathSpells = activeSpells.stream().filter(spell -> spell instanceof VengeanceSpell).map(spell -> (VengeanceSpell) spell).toList();
        if (!refusalOfDeathSpells.isEmpty()) {
            refusalOfDeathSpells.forEach(vengeanceSpell -> vengeanceSpell.onDamage(source, amount));
            return;
        }
        super.applyDamage(source, amount);
    }
}
