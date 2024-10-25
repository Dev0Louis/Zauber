package dev.louis.zauber.networking.play.c2s;

import dev.louis.zauber.Zauber;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ThrowTelekinedPayload() implements CustomPayload {
    public static final Id<ThrowTelekinedPayload> ID = new Id<>(Identifier.of(Zauber.MOD_ID, "throw_telekined"));
    public static final ThrowTelekinedPayload INSTANCE = new ThrowTelekinedPayload();
    public static final PacketCodec<PacketByteBuf, ThrowTelekinedPayload> CODEC = PacketCodec.unit(INSTANCE);


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
