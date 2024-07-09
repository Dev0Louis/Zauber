package dev.louis.zauber.mixin;

import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.fix.ItemStackComponentizationFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStackComponentizationFix.class)
public class ItemStackComponentizationFixMixin {
    @Inject(
            method = "fixStack",
            at = @At("RETURN")
    )
    private static void migrateNbtToComponents(ItemStackComponentizationFix.StackData data, Dynamic<?> dynamic, CallbackInfo ci) {
        data.getAndRemove("spell").result().ifPresent(oldDynamic -> {
            var spellType = dynamic.emptyMap().set("spell_type", oldDynamic);
            data.setComponent("zauber:stored_spell", spellType);
        });
        data.getAndRemove("lostBookId").result().ifPresent(oldDynamic -> {
            var lostBookId = dynamic.emptyMap().set("id", oldDynamic);
            data.setComponent("zauber:lost_book_id", lostBookId);
        });
        data.moveToComponent("stored_entity", "minecraft:entity_data");
    }
}
