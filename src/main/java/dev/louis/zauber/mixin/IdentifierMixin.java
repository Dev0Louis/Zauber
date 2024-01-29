package dev.louis.zauber.mixin;

import dev.louis.zauber.Zauber;
import dev.louis.zauber.config.ZauberConfig;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Identifier.class)
public class IdentifierMixin {

    @ModifyVariable(
            method = "<init>(Ljava/lang/String;Ljava/lang/String;Lnet/minecraft/util/Identifier$ExtraData;)V",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private static String convertToZauber(String namespace) {
        if(!ZauberConfig.HAS_CONFIG_LOADED || ZauberConfig.shouldConvertOldNamespace()) {
            return namespace.equals("chainsmpspells") ? Zauber.MOD_ID : namespace;
        }

        return namespace;
    }
}
