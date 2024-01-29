package dev.louis.zauber.networking;

import dev.louis.zauber.Zauber;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record OptionSyncPacket(int targetingDistance) implements FabricPacket {
    public static PacketType<OptionSyncPacket> TYPE = PacketType.create(Identifier.of(Zauber.MOD_ID, "option_sync"), OptionSyncPacket::read);

    private static OptionSyncPacket read(PacketByteBuf buf) {
        var configVersion = buf.readInt();
        if(configVersion != Zauber.VERSION)
            throw new IllegalStateException("The config version (" + configVersion + ") does not match clients version (" + Zauber.VERSION + ")!");

        return new OptionSyncPacket(buf.readInt());
    }


    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(Zauber.VERSION);
        buf.writeInt(targetingDistance);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
