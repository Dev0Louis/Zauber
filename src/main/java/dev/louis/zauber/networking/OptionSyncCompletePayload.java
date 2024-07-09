package dev.louis.zauber.networking;

import dev.louis.zauber.Zauber;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record OptionSyncCompletePayload() implements CustomPayload {
    public static Id<OptionSyncCompletePayload> ID = new Id<>(Identifier.of(Zauber.MOD_ID, "option_sync_complete"));
    public static final PacketCodec<PacketByteBuf, OptionSyncCompletePayload> CODEC = PacketCodec.of(OptionSyncCompletePayload::write, OptionSyncCompletePayload::read);

    private static OptionSyncCompletePayload read(PacketByteBuf buf) {
        return new OptionSyncCompletePayload();
    }

    public void write(PacketByteBuf buf) {

    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
