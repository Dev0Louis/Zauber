package dev.louis.zauber.item;

import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoryItem;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class TotemOfDarknessItem extends AccessoryItem {
    public TotemOfDarknessItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean canEquip(ItemStack stack, SlotReference ref) {
        return !ref.capability().isAnotherEquipped(stack, ref, this);
    }

    public static boolean isActive(LivingEntity entity) {
        if (entity.getWorld().isClient()) {
            //As on the client it can be called on the render thread
            entity.getWorld().calculateAmbientDarkness();
        }
        return AccessoriesCapability.getOptionally(entity).map(capability -> capability.isEquipped(ZauberItems.TOTEM_OF_DARKNESS)).orElse(false) && entity.getWorld().getLightLevel(entity.getBlockPos()) <= HeartOfTheDarknessItem.MAX_BRIGHTNESS;
    }
}
