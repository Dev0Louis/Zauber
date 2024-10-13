package dev.louis.zauber.networking.play.c2s;

import dev.louis.zauber.Zauber;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record StartTelekinesisPayload(TelekinesisTarget target) implements CustomPayload {
    public static final Id<StartTelekinesisPayload> ID = new Id<>(Identifier.of(Zauber.MOD_ID, "start_telekinesis"));
    public static final PacketCodec<PacketByteBuf, StartTelekinesisPayload> CODEC = PacketCodec.ofStatic(((buf, value) -> value.write(buf)), StartTelekinesisPayload::read);


    public StartTelekinesisPayload(BlockPos pos) {
        this(new TelekinesisTarget.BlockTarget(pos));
    }

    public StartTelekinesisPayload(Entity entity) {
        this(new TelekinesisTarget.EntityTarget(entity.getId()));
    }

    private static StartTelekinesisPayload read(PacketByteBuf buf) {
        TelekinesisTarget target = switch (buf.readVarInt()) {
            case 0 -> new TelekinesisTarget.BlockTarget(buf.readBlockPos());
            case 1 -> new TelekinesisTarget.EntityTarget(buf.readVarInt());
            default -> throw new IllegalStateException("Unexpected value: " + buf.readVarInt());
        };
        return new StartTelekinesisPayload(target);
    }

    private void write(PacketByteBuf buf) {
        switch (target) {
            case TelekinesisTarget.BlockTarget(BlockPos pos) -> {
                buf.writeByte(0);
                buf.writeBlockPos(pos);
            }
            case TelekinesisTarget.EntityTarget(int telekinedEntityId) -> {
                buf.writeByte(1);
                buf.writeVarInt(telekinedEntityId);
            }
        }
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public sealed interface TelekinesisTarget {
        record BlockTarget(BlockPos pos) implements TelekinesisTarget {

        }

        record EntityTarget(int telekinedEntityId) implements TelekinesisTarget {

        }
    }


}
