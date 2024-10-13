package dev.louis.zauber.extension;

import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

public interface ItemStackJuggernautModeExtension {
    static ItemStackJuggernautModeExtension access(ItemStack itemStack) {
        return (ItemStackJuggernautModeExtension) (Object) itemStack;
    }

    void zauber$setJuggernautModeTick(long ticks);

    long zauber$getJuggernautTick();

    boolean zauber$isJuggernautItem();

    boolean zauber$isInValid(ServerWorld world);

    boolean zauber$isValid(ServerWorld world);
}
