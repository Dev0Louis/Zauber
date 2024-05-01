package dev.louis.zauber.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Objects;

public final class ItemWithCount {
    private Item item;
    private int count;

    public ItemWithCount(Item item, int count) {
        this.item = item;
        this.count = count;
    }

    public Item item() {
        return item;
    }

    public int count() {
        return count;
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, count);
    }

    @Override
    public String toString() {
        return "ItemWithCount[" +
                "item=" + item + ", " +
                "count=" + count + ']';
    }

    public void canContain(ItemStack itemStack) {

    }
}
