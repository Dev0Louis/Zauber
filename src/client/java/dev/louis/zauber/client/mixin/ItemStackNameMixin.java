package dev.louis.zauber.client.mixin;

import dev.louis.zauber.component.ZauberDataComponentTypes;
import net.minecraft.component.ComponentHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackNameMixin implements ComponentHolder {


    @Inject(method = "getName", at = @At("HEAD"), cancellable = true)
    public void nameMixin(CallbackInfoReturnable<Text> cir) {
        var component = this.get(ZauberDataComponentTypes.STORED_SPELL_TYPE);
        if(component != null) {

            var splitId = component.spellType().getIdAsString().split(":");
            if (splitId.length < 2) return;
            cir.setReturnValue(Text.translatable("item."+ splitId[0] + "." + splitId[1] + "_spell_book"));

        }
    }
}
