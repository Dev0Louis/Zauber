package dev.louis.zauber.networking.play.c2s;

import dev.louis.zauber.Zauber;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record StopTelekinesisPayload(boolean place) implements CustomPayload {
    public static final Id<StopTelekinesisPayload> ID = new Id<>(Identifier.of(Zauber.MOD_ID, "stop_telekinesis"));
    public static final PacketCodec<PacketByteBuf, StopTelekinesisPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOL, StopTelekinesisPayload::place, StopTelekinesisPayload::new
    );


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
