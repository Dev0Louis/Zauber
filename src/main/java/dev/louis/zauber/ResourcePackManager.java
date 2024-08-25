package dev.louis.zauber;

import eu.pb4.polymer.common.api.PolymerCommonUtils;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.fabric.impl.resource.loader.ModResourcePackUtil;

public class ResourcePackManager {
    public static void init() {
        PolymerResourcePackUtils.addModAssets(Zauber.MOD_ID);
        PolymerResourcePackUtils.markAsRequired();
    }
}
