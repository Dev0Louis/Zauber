package dev.louis.zauber.networking.configuration.s2c;

import dev.louis.zauber.Zauber;
import dev.louis.zauber.config.ConfigManager;
import dev.louis.zauber.config.ServerConfig;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record OptionSyncPayload(ServerConfig overrideConfig) implements CustomPayload {
    public static Id<OptionSyncPayload> ID = new Id<>(Identifier.of(Zauber.MOD_ID, "option_sync"));
    public static final PacketCodec<PacketByteBuf, OptionSyncPayload> CODEC = PacketCodec.of(OptionSyncPayload::write, OptionSyncPayload::read);

    private static OptionSyncPayload read(PacketByteBuf buf) {
        return new OptionSyncPayload(ConfigManager.readOverrideConfig(buf));
    }

    public void write(PacketByteBuf buf) {
        ConfigManager.writeOverrideConfig(buf, overrideConfig);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
