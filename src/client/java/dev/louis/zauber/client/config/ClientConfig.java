package dev.louis.zauber.client.config;


import com.bawnorton.configurable.Configurable;
import dev.louis.zauber.mana.ManaDirection;

import java.awt.*;

public class ClientConfig {
    @Configurable
    private static ManaDirection manaDirection = ManaDirection.RIGHT;
    @Configurable
    public static int targetingColor = Color.RED.getRGB();

}
