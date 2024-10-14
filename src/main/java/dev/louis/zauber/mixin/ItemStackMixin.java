package dev.louis.zauber.mixin;

import dev.louis.zauber.component.item.ZauberDataComponentTypes;
import dev.louis.zauber.component.item.type.JuggernautTickComponent;
import dev.louis.zauber.extension.ItemStackJuggernautModeExtension;
import net.minecraft.component.ComponentHolder;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ItemStackJuggernautModeExtension, ComponentHolder {
    @Shadow
    public abstract void setCount(int count);

    @Shadow
    @Nullable
    public abstract <T> T set(ComponentType<? super T> type, @Nullable T value);

    @Inject(method = "inventoryTick", at = @At("TAIL"))
    public void removeAfterExpiration(World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (world instanceof ServerWorld serverWorld) {
            if (zauber$isJuggernautItem() && zauber$isInValid(serverWorld)) {
                this.setCount(0);
            }
        }
    }

    @Override
    public void zauber$setJuggernautModeTick(long ticks) {
        this.set(ZauberDataComponentTypes.JUGGERNAUT_TICK, new JuggernautTickComponent(ticks));
    }

    @Override
    public long zauber$getJuggernautTick() {
        JuggernautTickComponent component = this.get(ZauberDataComponentTypes.JUGGERNAUT_TICK);

        if (component == null) return 0L;
        return component.deletionTick();
    }

    @Override
    public boolean zauber$isJuggernautItem() {
        return this.contains(ZauberDataComponentTypes.JUGGERNAUT_TICK);
    }

    public boolean zauber$isInValid(ServerWorld world) {
        return !zauber$isValid(world);
    }

    public boolean zauber$isValid(ServerWorld world) {
        long juggernautTicks = zauber$getJuggernautTick();
        if (juggernautTicks == 0L) return false;
        return (((ServerWorldAccessor) world).getWorldProperties().getTime() - zauber$getJuggernautTick()) < /*ConfigManager.getServerConfig().juggernautSpellDuration();*/ 1;
    }


}
