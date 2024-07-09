package dev.louis.zauber.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Objects;

public final class SimpleOwnableImmutableSingleStackInventory implements SingleStackInventory {
    private final PlayerEntity player;
    private final ItemStack itemStack;

    public SimpleOwnableImmutableSingleStackInventory(PlayerEntity player, ItemStack itemStack) {
        this.player = player;
        this.itemStack = itemStack;
    }

    @Override
    public ItemStack getStack() {
        return itemStack;
    }

    @Override
    public ItemStack decreaseStack(int count) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.closeHandledScreen();
        }
        return itemStack;
    }

    @Override
    public void setStack(ItemStack stack) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean canPlayerUse(PlayerEntity user) {
        return user == player;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (SimpleOwnableImmutableSingleStackInventory) obj;
        return Objects.equals(this.itemStack, that.itemStack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemStack);
    }

    @Override
    public String toString() {
        return "SimpleImmutableSingleStackInventory[" +
                "itemStack=" + itemStack + ']';
    }

}
