package dev.louis.zauber.poi;

import dev.louis.zauber.Zauber;
import dev.louis.zauber.block.ZauberBlocks;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.poi.PointOfInterestType;

public class ZauberPointOfInterestTypes {
    /**
     Does not cover the Ritual Stone block as this POI is used to search for block out of the perspective of the Ritual Stone.
     */
    public static final PointOfInterestType RITUAL_BLOCKS =
            PointOfInterestHelper.register(Identifier.of(Zauber.MOD_ID, "ritual_blocks"), 1, 20, ZauberBlocks.ITEM_SACRIFICER, ZauberBlocks.MANA_STORAGE);
    public static final RegistryKey<PointOfInterestType> RITUAL_BLOCKS_KEY =
            Registries.POINT_OF_INTEREST_TYPE.getKey(RITUAL_BLOCKS).orElseThrow();

    public static void init() {

    }
}
