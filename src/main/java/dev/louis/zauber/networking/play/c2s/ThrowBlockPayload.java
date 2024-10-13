package dev.louis.zauber.networking.play.c2s;

import dev.louis.zauber.Zauber;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ThrowBlockPayload() implements CustomPayload {
    public static final Id<ThrowBlockPayload> ID = new Id<>(Identifier.of(Zauber.MOD_ID, "throw_block"));
    public static final PacketCodec<PacketByteBuf, ThrowBlockPayload> CODEC = PacketCodec.unit(INSTANCE);

    public static final ThrowBlockPayload INSTANCE = new ThrowBlockPayload();

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
