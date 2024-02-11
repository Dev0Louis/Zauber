package dev.louis.zauber.networking;

import dev.louis.zauber.Zauber;
import dev.louis.zauber.config.ConfigManager;
import dev.louis.zauber.config.ServerConfig;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record OptionSyncPacket(ServerConfig overrideConfig) implements FabricPacket {
    public static PacketType<OptionSyncPacket> TYPE = PacketType.create(Identifier.of(Zauber.MOD_ID, "option_sync"), OptionSyncPacket::read);

    private static OptionSyncPacket read(PacketByteBuf buf) {
        return new OptionSyncPacket(ConfigManager.readOverrideConfig(buf));
    }

    @Override
    public void write(PacketByteBuf buf) {
        ConfigManager.writeOverrideConfig(buf, overrideConfig);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
