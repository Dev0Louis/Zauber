package dev.louis.zauber.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Box;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class Debugger {
    private static final Map<Entity, List<Pair<Box, Color>>> BOX_LIST = new HashMap<>();

    public static void addEntityBoundBox(Entity entity, Box box, Color color) {
        addEntityBoundBox(entity, List.of(new Pair<>(box, color)));
    }


        // Will be removed next deletionTick.
    public static void addEntityBoundBox(Entity entity, List<Pair<Box, Color>> listBox) {
        if (FabricLoader.getInstance().isDevelopmentEnvironment() && FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            BOX_LIST.put(entity, listBox);
        }
    }

    public static void check(boolean isInWorld) {
        if (!isInWorld) {
            BOX_LIST.clear();
        }
        List.copyOf(BOX_LIST.keySet()).stream().filter(entity -> !entity.isAlive()).forEach(BOX_LIST::remove);
    }

    public static Collection<List<Pair<Box, Color>>> getBoxList() {
        return BOX_LIST.values();
    }
}
