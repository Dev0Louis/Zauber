package dev.louis.zauber.component.type;

import dev.louis.zauber.Zauber;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import xyz.nucleoid.packettweaker.PacketContext;

public interface ZauberComponent extends PolymerComponent {
    @Override
    default boolean canSyncRawToClient(PacketContext context) {
        return Zauber.isClientModded(context.getPlayer());
    }
}
