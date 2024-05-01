package dev.louis.zauber.items;

import dev.louis.zauber.ritual.RitualType;
import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public class HorseRitualScrollItem extends Item implements PolymerItem, RitualItem {
    public HorseRitualScrollItem(Settings settings) {
        super(settings);
    }

    @Override
    public RitualType<?> getRitualType() {
        return RitualType.MANA_HORSE;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return Items.PAPER;
    }
}
