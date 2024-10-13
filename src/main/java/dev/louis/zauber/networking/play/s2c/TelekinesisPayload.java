package dev.louis.zauber.networking.play.s2c;

import dev.louis.zauber.Zauber;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;

public record TelekinesisPayload(int playerId, OptionalInt telekinesedEntityId) implements CustomPayload {
    public static Id<TelekinesisPayload> ID = new Id<>(Identifier.of(Zauber.MOD_ID, "throw_block"));

    public static final PacketCodec<PacketByteBuf, TelekinesisPayload> CODEC = PacketCodec.ofStatic(((buf, value) -> value.write(buf)), TelekinesisPayload::read);

    public TelekinesisPayload(PlayerEntity player, @Nullable Entity telekinesedEntity) {
        this(player.getId(), telekinesedEntity != null ? OptionalInt.of(telekinesedEntity.getId()) : OptionalInt.empty());
    }

    private static TelekinesisPayload read(PacketByteBuf buf) {
        var id = buf.readVarInt();
        var isTelkinesing = buf.readBoolean();
        if (isTelkinesing) {
            return new TelekinesisPayload(id, OptionalInt.of(buf.readVarInt()));
        }
        return new TelekinesisPayload(id, OptionalInt.empty());
    }

    private void write(PacketByteBuf buf) {
        buf.writeVarInt(playerId);
        buf.writeBoolean(telekinesedEntityId.isPresent());
        if (telekinesedEntityId.isPresent()) {
            buf.writeVarInt(telekinesedEntityId.getAsInt());
        }
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
