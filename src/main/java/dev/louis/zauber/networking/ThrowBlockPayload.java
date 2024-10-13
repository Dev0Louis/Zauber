package dev.louis.zauber.networking;

import dev.louis.zauber.Zauber;
import dev.louis.zauber.config.ConfigManager;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.s2c.config.ReadyS2CPacket;
import net.minecraft.util.Identifier;

public record ThrowBlockPayload() implements CustomPayload {
    public static Id<ThrowBlockPayload> ID = new Id<>(Identifier.of(Zauber.MOD_ID, "throw_block"));
    public static final ThrowBlockPayload INSTANCE = new ThrowBlockPayload();

    public static final PacketCodec<PacketByteBuf, ThrowBlockPayload> CODEC = PacketCodec.unit(INSTANCE);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
