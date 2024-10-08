package dev.louis.zauber.client.config;

import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.louis.zauber.config.ConfigManager;
import net.minecraft.client.gui.screen.Screen;

public class ClientConfigManager extends ConfigManager {


    public static Screen createScreen(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(yaclText("title", "main"))
                .categories(generateCategories())
                .save(() -> {
                    write(serverConfig, SERVER_PATH);
                    if (clientConfig != null) {
                        write(clientConfig, CLIENT_PATH);
                    }
                })
                .build()
                .generateScreen(parent);
    }
}
