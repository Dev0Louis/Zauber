package dev.louis.zauber.mixin;

import dev.emi.trinkets.api.LivingEntityTrinketComponent;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LivingEntityTrinketComponent.class)
public abstract class LivingEntityTrinketComponentMixin implements AutoSyncedComponent {
    @Override
    public boolean isRequiredOnClient() {
        return false;
    }
}
