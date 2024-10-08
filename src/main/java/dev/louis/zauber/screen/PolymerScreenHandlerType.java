package dev.louis.zauber.screen;

import dev.louis.zauber.Zauber;
import eu.pb4.polymer.core.api.utils.PolymerSyncedObject;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;

public class PolymerScreenHandlerType<T extends ScreenHandler> extends ScreenHandlerType<T> implements PolymerSyncedObject<ScreenHandlerType<?>> {
    private final ScreenHandlerType<?> fakeScreenHandlerType;

    public PolymerScreenHandlerType(Factory<T> factory, FeatureSet requiredFeatures, ScreenHandlerType<?> fakeScreenHandlerType) {
        super(factory, requiredFeatures);
        this.fakeScreenHandlerType = fakeScreenHandlerType;
    }

    @Override
    public ScreenHandlerType<?> getPolymerReplacement(ServerPlayerEntity player) {
        if (Zauber.isClientModded(player)) return this;
        return fakeScreenHandlerType;
    }
}
