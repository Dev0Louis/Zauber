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

public record TelekinesisStatePayload(int playerId, OptionalInt telekinesedEntityId) implements CustomPayload {
    public static Id<TelekinesisStatePayload> ID = new Id<>(Identifier.of(Zauber.MOD_ID, "player_telekinesis_target"));

    public static final PacketCodec<PacketByteBuf, TelekinesisStatePayload> CODEC = PacketCodec.ofStatic(((buf, value) -> value.write(buf)), TelekinesisStatePayload::read);

    public TelekinesisStatePayload(PlayerEntity player, @Nullable Entity telekinesedEntity) {
        this(player.getId(), telekinesedEntity != null ? OptionalInt.of(telekinesedEntity.getId()) : OptionalInt.empty());
    }

    private static TelekinesisStatePayload read(PacketByteBuf buf) {
        var id = buf.readVarInt();
        var isTelkinesing = buf.readBoolean();
        if (isTelkinesing) {
            return new TelekinesisStatePayload(id, OptionalInt.of(buf.readVarInt()));
        }
        return new TelekinesisStatePayload(id, OptionalInt.empty());
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
