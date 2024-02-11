package dev.louis.zauber.duck;

import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

public interface ItemStackJuggernautModeDuck {
    static ItemStackJuggernautModeDuck access(ItemStack itemStack) {
        return (ItemStackJuggernautModeDuck) (Object) itemStack;
    }
    void zauber$setJuggernautModeTick(long ticks);
    long zauber$getJuggernautTick();
    boolean zauber$isJuggernautItem();
    boolean zauber$isInValid(ServerWorld world);
    boolean zauber$isValid(ServerWorld world);
}
