package dev.louis.zauber.networking;

import dev.louis.zauber.Zauber;
import eu.pb4.polymer.networking.api.ContextByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ICanHasZauberPayload() implements CustomPayload {
    public static final Id<ICanHasZauberPayload> ID = new Id<>(Identifier.of(Zauber.MOD_ID, "icanhaszauber"));
    public static final PacketCodec<ContextByteBuf, ICanHasZauberPayload> CODEC = PacketCodec.of(ICanHasZauberPayload::write, ICanHasZauberPayload::read);

    private static ICanHasZauberPayload read(ContextByteBuf buf) {
        return new ICanHasZauberPayload();
    }

    private void write(ContextByteBuf buf) {

    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
