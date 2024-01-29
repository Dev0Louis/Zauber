package dev.louis.zauber.networking;

import dev.louis.zauber.Zauber;
import eu.pb4.polymer.networking.api.payload.VersionedPayload;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import xyz.nucleoid.packettweaker.PacketContext;

public record ICanHasZauberPayload() implements VersionedPayload {
    public static final Identifier ID = new Identifier(Zauber.MOD_ID, "icanhas");

    @Override
    public void write(PacketContext context, int version, PacketByteBuf buf) {
    }

    @Override
    public Identifier id() {
        return ID;
    }

    public static ICanHasZauberPayload read(PacketContext context, Identifier identifier, int version, PacketByteBuf buf) {
        return new ICanHasZauberPayload();
    }
}
