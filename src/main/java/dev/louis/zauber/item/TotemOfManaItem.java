package dev.louis.zauber.item;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.SlotType;
import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class TotemOfManaItem extends Item implements Trinket {
    public TotemOfManaItem(Item.Settings settings) {
        super(settings);
        TrinketsApi.registerTrinket(this, this);
    }


    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    @Override
    public boolean canEquip(ItemStack stack, SlotReference ref, LivingEntity entity) {
        SlotType slot = ref.inventory().getSlotType();
        var containsItem = TrinketsApi.getTrinketComponent(entity).map(component -> component.getInventory().get(slot.getGroup()).get(slot.getName()).containsAny(stack1 -> stack1.getItem().equals(stack.getItem()))).orElse(false);
        return !containsItem;
    }

    public static boolean isActive(LivingEntity entity) {
        return TrinketsApi.getTrinketComponent(entity)
                .map(component ->
                        component.getEquipped(ZauberItems.TOTEM_OF_MANA).stream()
                                .map(Pair::getRight).anyMatch(stack -> stack.getDamage() + 1 < stack.getMaxDamage())
                ).orElse(false);
    }
}
