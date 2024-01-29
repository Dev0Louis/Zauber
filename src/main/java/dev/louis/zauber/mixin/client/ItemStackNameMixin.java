package dev.louis.zauber.mixin.client;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackNameMixin {

    @Shadow public abstract @Nullable NbtCompound getNbt();

    @Inject(method = "getName", at = @At("HEAD"), cancellable = true)
    public void nameMixin(CallbackInfoReturnable<Text> cir) {
        if(this.getNbt() != null) {
            if (this.getNbt().contains("spell")) {
                var splitId = this.getNbt().getString("spell").split(":");
                if(splitId.length < 2)return;
                cir.setReturnValue(Text.translatable("item."+ splitId[0] + "." + splitId[1] + "_spell_book"));
            }
        }
    }
}
