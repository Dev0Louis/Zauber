package dev.louis.zauber.item;

import io.wispforest.accessories.api.AccessoryItem;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class TotemOfIceItem extends AccessoryItem {

    public TotemOfIceItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean canEquip(ItemStack stack, SlotReference ref) {
        //noinspection DataFlowIssue
        return !ref.capability().isAnotherEquipped(stack, ref, this);
    }

    public static boolean isActive(LivingEntity entity) {
        return !entity.getWorld().getDimension().ultrawarm();
    }
}
