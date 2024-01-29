package dev.louis.zauber.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import dev.louis.zauber.accessor.ItemStackJuggernautModeAccessor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ItemStackJuggernautModeAccessor {
    @Shadow public abstract @Nullable NbtCompound getNbt();
    @Shadow public abstract void setCount(int count);
    @Shadow public abstract NbtCompound getOrCreateNbt();

    @Inject(method = "inventoryTick", at = @At("TAIL"))
    public void removeAfterExpiration(World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if(world instanceof ServerWorld serverWorld) {
            if(zauber$isJuggernautItem() && zauber$isInValid(serverWorld)) {
                this.setCount(0);
            }
        }
    }
    @Override
    public void zauber$setJuggernautModeTick(long ticks) {
        NbtCompound nbt = this.getOrCreateNbt();
        if(nbt == null)return;
        nbt.putLong("JuggernautTicks", ticks);
    }

    @Override
    public long zauber$getJuggernautTick() {
        if(this.getNbt() == null)return 0L;
        return this.getNbt().getLong("JuggernautTicks");
    }

    @Override
    public boolean zauber$isJuggernautItem() {
        if(this.getNbt() == null)return false;
        return this.getNbt().contains("JuggernautTicks");
    }

    public boolean zauber$isInValid(ServerWorld world) {
        return !zauber$isValid(world);
    }

    public boolean zauber$isValid(ServerWorld world) {
        long juggernautTicks = zauber$getJuggernautTick();
        if(juggernautTicks == 0L)return false;
        return (((ServerWorldAccessor) world).getWorldProperties().getTime()- zauber$getJuggernautTick())<20*90;
    }


}
