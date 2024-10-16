package dev.louis.zauber.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.louis.nebula.api.spell.holder.SpellEffectHolder;
import dev.louis.zauber.extension.EntityExtension;
import dev.louis.zauber.spell.effect.type.SpellEffectTypes;
import io.wispforest.accessories.api.AccessoriesCapability;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

@Debug(export = true)
@Mixin(value = LivingEntity.class, priority = 1001)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @ModifyReturnValue(
            method = "isPushable", at = @At("RETURN"))
    public boolean dashingPlayersAreNotPushable(boolean original) {
        //TODO: Add Nebula way to check for Type
        return original && !((Object) this instanceof SpellEffectHolder spellEffectHolder && spellEffectHolder.getSpellEffects().stream().anyMatch(spellEffect -> spellEffect.getType().equals(SpellEffectTypes.DASH)));
    }

    /*@Inject(
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
    }*/

    @SuppressWarnings({"InvalidInjectorMethodSignature", "UnreachableCode"})
    @ModifyVariable(
            method = "tryUseTotem",
            at = @At(value = "LOAD", opcode = 0),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;decrement(I)V"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;incrementStat(Lnet/minecraft/stat/Stat;)V")
            ),
            ordinal = 0,
            allow = 1
    )
    public ItemStack modifyItemStack(ItemStack itemStack) {
        if (itemStack == null) {
            if (((Object) this) instanceof PlayerEntity player) {
                var totem = AccessoriesCapability.get(player).getFirstEquipped(Items.TOTEM_OF_UNDYING);

                if (totem != null) {
                    ItemStack slotStack = totem.stack();
                    ItemStack stackCopy = slotStack.copy();
                    slotStack.decrement(1);
                    return stackCopy;
                }
            }
        }
        return itemStack;
    }

    @ModifyExpressionValue(
            method = "computeFallDamage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityType;isIn(Lnet/minecraft/registry/tag/TagKey;)Z")
    )
    public boolean a(boolean isImmune) {
        return isImmune || ((EntityExtension) this).isTelekinesed();
    }
}
