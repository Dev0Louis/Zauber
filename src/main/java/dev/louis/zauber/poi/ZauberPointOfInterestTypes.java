package dev.louis.zauber.poi;

import dev.louis.zauber.Zauber;
import dev.louis.zauber.block.ZauberBlocks;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.poi.PointOfInterestType;

public class ZauberPointOfInterestTypes {

    public static final PointOfInterestType RITUAL_BLOCK =
            PointOfInterestHelper.register(Identifier.of(Zauber.MOD_ID, "ritual_block"), 1, 20, ZauberBlocks.RITUAL_STONE);
    public static final RegistryKey<PointOfInterestType> RITUAL_BLOCK_KEY =
            Registries.POINT_OF_INTEREST_TYPE.getKey(RITUAL_BLOCK).orElseThrow();


    public static final PointOfInterestType ITEM_SACRIFICERS =
            PointOfInterestHelper.register(Identifier.of(Zauber.MOD_ID, "item"), 1, 20, ZauberBlocks.ITEM_SACRIFICER);
    public static final RegistryKey<PointOfInterestType> ITEM_SACRIFICERS_KEY =
            Registries.POINT_OF_INTEREST_TYPE.getKey(ITEM_SACRIFICERS).orElseThrow();

    public static final PointOfInterestType MANA_CAULDRON =
            PointOfInterestHelper.register(Identifier.of(Zauber.MOD_ID, "mana_cauldron"), 1, 20, ZauberBlocks.MANA_CAULDRON);
    public static final RegistryKey<PointOfInterestType> MANA_CAULDRON_KEY =
            Registries.POINT_OF_INTEREST_TYPE.getKey(MANA_CAULDRON).orElseThrow();


    public static final PointOfInterestType DARKNESS_ACCUMULATOR =
            PointOfInterestHelper.register(Identifier.of(Zauber.MOD_ID, "darkness_accumulator"), 1, 20, ZauberBlocks.DARKNESS_ACCUMULATOR);
    public static final RegistryKey<PointOfInterestType> DARKNESS_ACCUMULATOR_KEY =
            Registries.POINT_OF_INTEREST_TYPE.getKey(DARKNESS_ACCUMULATOR).orElseThrow();

    public static void init() {

    }
}
