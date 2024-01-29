package dev.louis.zauber.accessor;

import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

public interface ItemStackJuggernautModeAccessor {
    static ItemStackJuggernautModeAccessor access(ItemStack itemStack) {
        return (ItemStackJuggernautModeAccessor) (Object) itemStack;
    }
    void zauber$setJuggernautModeTick(long ticks);
    long zauber$getJuggernautTick();
    boolean zauber$isJuggernautItem();
    boolean zauber$isInValid(ServerWorld world);
    boolean zauber$isValid(ServerWorld world);
}
