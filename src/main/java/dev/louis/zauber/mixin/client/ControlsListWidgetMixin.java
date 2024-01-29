package dev.louis.zauber.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.louis.zauber.keybind.SpellKeyBinding;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Arrays;

@Mixin(ControlsListWidget.class)
public class ControlsListWidgetMixin {
    @ModifyExpressionValue(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lorg/apache/commons/lang3/ArrayUtils;clone([Ljava/lang/Object;)[Ljava/lang/Object;")
    )
    public Object[] a(Object[] original) {
        return Arrays.stream(original).filter(keyBinding -> !(keyBinding instanceof SpellKeyBinding) || ((SpellKeyBinding) keyBinding).shouldShow()).toArray(KeyBinding[]::new);
    }
}
