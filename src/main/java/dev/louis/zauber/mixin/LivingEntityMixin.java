package dev.louis.zauber.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketsApi;
import dev.louis.nebula.api.NebulaPlayer;
import dev.louis.zauber.Zauber;
import dev.louis.zauber.spell.VengeanceSpell;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Pair;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntity.class, priority = 1001)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

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

    @SuppressWarnings("InvalidInjectorMethodSignature")
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
                var artifactTotemSlotType = TrinketsApi.getPlayerSlots(player).get("artifact").getSlots().get("totem");
                var component = TrinketsApi.getTrinketComponent((LivingEntity) (Object) this);
                if (component.isPresent()) {

                    var b = component.get().getEquipped(Items.TOTEM_OF_UNDYING);
                    for (Pair<SlotReference, ItemStack> slotReferenceItemStackPair : b) {

                        if (slotReferenceItemStackPair.getLeft().inventory().getSlotType().equals(artifactTotemSlotType)) {
                            ItemStack slotStack = slotReferenceItemStackPair.getRight();
                            ItemStack stackCopy = slotStack.copy();
                            slotStack.decrement(1);
                            return stackCopy;
                        }
                    }
                }
            }
        }
        return itemStack;
    }
}
