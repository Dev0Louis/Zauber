package dev.louis.zauber.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.louis.zauber.helper.ShutUpAboutBlockStateModels;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BlockStatesLoader;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Debug(export = true)
@Mixin(BlockStatesLoader.class)
public class BlockStatesLoaderMixin {
    @WrapOperation(
            method = "method_61066",
            at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V")
    )
    private void a(Logger instance, String string, Object o, Object o1, Operation<Void> original, @Local(argsOnly = true) BlockState state) {
        if (!state.getBlock().getClass().isAnnotationPresent(ShutUpAboutBlockStateModels.class)) {
            original.call(instance, string, o, o1);
        }
    }
}
