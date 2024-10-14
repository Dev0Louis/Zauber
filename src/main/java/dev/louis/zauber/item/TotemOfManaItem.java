package dev.louis.zauber.item;

import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoryItem;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class TotemOfManaItem extends AccessoryItem {

    public TotemOfManaItem(Item.Settings settings) {
        super(settings);
    }


    @Override
    public boolean canEquip(ItemStack stack, SlotReference ref) {
        return !ref.capability().isAnotherEquipped(stack, ref, this);
    }

    public static boolean isActive(LivingEntity entity) {
        return AccessoriesCapability.getOptionally(entity)
                .map(capability ->
                        capability.getEquipped(ZauberItems.TOTEM_OF_MANA).stream()
                                .map(SlotEntryReference::stack).anyMatch(stack -> stack.getDamage() + 1 < stack.getMaxDamage())
                ).orElse(false);
    }
}
