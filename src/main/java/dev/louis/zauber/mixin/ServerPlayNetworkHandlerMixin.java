package dev.louis.zauber.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.louis.zauber.accessor.ItemStackJuggernautModeAccessor.access;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    @Shadow public ServerPlayerEntity player;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandler;disableSyncing()V"),
            method = "onClickSlot", cancellable = true)
    public void onClickSlot(ClickSlotC2SPacket packet, CallbackInfo ci) {
        if(packet.getSlot() < -1 && packet.getSlot() == -999) return;

        var modifiedStack = packet.getModifiedStacks();
        if(modifiedStack.isEmpty())return;
        var size = modifiedStack.size();

        boolean shouldCancel = false;
        if(size == 1) {
            ItemStack itemStack = modifiedStack.values().stream().toList().get(0);
            shouldCancel = access(itemStack).zauber$isJuggernautItem();
        }else if (size == 2) {
            ItemStack itemStack = modifiedStack.values().stream().toList().get(0);
            ItemStack itemStack2 = modifiedStack.values().stream().toList().get(1);
            shouldCancel = access(itemStack).zauber$isJuggernautItem() || access(itemStack2).zauber$isJuggernautItem();
        }

        if(shouldCancel) {
            ci.cancel();
            player.currentScreenHandler.syncState();
        }
    }
}
