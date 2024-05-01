package dev.louis.zauber.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.stream.Stream;

public class ItemCollector {
    private final HashMap<Item, Integer> items = new HashMap<>();

    public void collect(ItemStack stack) {
        this.items.put(stack.getItem(), this.items.getOrDefault(stack.getItem(), 0) + stack.getCount());
    }

    public Stream<ItemWithCount> getItems() {
        return items.entrySet().stream().map(itemIntegerEntry -> new ItemWithCount(itemIntegerEntry.getKey(), itemIntegerEntry.getValue()));
    }
}
