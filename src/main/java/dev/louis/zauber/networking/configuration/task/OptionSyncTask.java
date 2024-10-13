package dev.louis.zauber.networking.configuration.task;

import dev.louis.zauber.config.ConfigManager;
import dev.louis.zauber.networking.configuration.s2c.OptionSyncPayload;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerConfigurationTask;

import java.util.function.Consumer;

public class OptionSyncTask implements ServerPlayerConfigurationTask {
    public static final Key KEY = new Key(OptionSyncPayload.ID.id().toString());

    @Override
    public void sendPacket(Consumer<Packet<?>> sender) {
        var packet = new OptionSyncPayload(ConfigManager.getServerConfig());
        sender.accept(ServerConfigurationNetworking.createS2CPacket(packet));
    }

    @Override
    public Key getKey() {
        return KEY;
    }
}
