package dev.louis.zauber.component.type;

import dev.louis.zauber.Zauber;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import xyz.nucleoid.packettweaker.PacketContext;

public interface ZauberComponent extends PolymerComponent {
    @Override
    default boolean canSyncRawToClient(PacketContext context) {
        var a = Zauber.isClientModded(context.getPlayer());
        System.out.println(context.getPlayer() + " " + a);
        return a;
    }
}
