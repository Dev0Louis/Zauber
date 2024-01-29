package dev.louis.zauber.networking;

import dev.louis.zauber.Zauber;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record OptionSyncCompletePacket() implements FabricPacket {
    public static PacketType<OptionSyncCompletePacket> TYPE = PacketType.create(Identifier.of(Zauber.MOD_ID, "option_sync_complete"), OptionSyncCompletePacket::read);

    private static OptionSyncCompletePacket read(PacketByteBuf buf) {
        return new OptionSyncCompletePacket();
    }


    @Override
    public void write(PacketByteBuf buf) {

    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
