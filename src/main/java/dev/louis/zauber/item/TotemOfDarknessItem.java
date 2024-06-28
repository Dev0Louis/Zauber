package dev.louis.zauber.item;

import dev.louis.zauber.Zauber;
import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public class TotemOfDarknessItem extends Item implements PolymerItem {
    public TotemOfDarknessItem(Settings settings) {
        super(settings);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return Zauber.isClientModded(player) ? itemStack.getItem() : Items.TOTEM_OF_UNDYING;
    }
}
