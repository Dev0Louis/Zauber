package dev.louis.zauber.item;

import dev.louis.zauber.Zauber;
import dev.louis.zauber.block.ZauberBlocks;
import eu.pb4.polymer.core.api.item.PolymerBlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.ArrayList;
import java.util.List;

public class ZauberItems {
    public static final List<Item> IN_CREATIVE_INVENTORY = new ArrayList<>();

    public static final Item SPELL_BOOK =
            registerNoCreativeTab("spell_book", new SpellBookItem(new Item.Settings().fireproof().rarity(Rarity.RARE)));
    public static final Item SOUL_HORN =
            register("soul_horn", new SoulHornItem(new Item.Settings().rarity(Rarity.RARE)));
    public static final Item SPELL_TABLE =
            register("spell_table", new SpellTableItem(ZauberBlocks.SPELL_TABLE, new Item.Settings().rarity(Rarity.EPIC)));
    public static final Item HEART_OF_THE_ICE =
            register("heart_of_the_ice", new HeartOfTheIceItem(new Item.Settings().rarity(Rarity.UNCOMMON)));
    public static final Item HEART_OF_THE_DARKNESS =
            register("heart_of_the_darkness", new HeartOfTheDarknessItem(new Item.Settings().rarity(Rarity.UNCOMMON)));
    public static final Item RITUAL_STONE =
            register("ritual_stone", new PolymerBlockItem(ZauberBlocks.RITUAL_STONE, new Item.Settings().rarity(Rarity.RARE), Items.LODESTONE));
    public static final Item ITEM_SACRIFICER =
            register("item_sacrificer", new PolymerBlockItem(ZauberBlocks.ITEM_SACRIFICER, new Item.Settings().rarity(Rarity.RARE), Items.STONE_BRICK_WALL));
    public static final Item DARKNESS_ACCUMULATOR =
            register("darkness_accumulator", new PolymerBlockItem(ZauberBlocks.DARKNESS_ACCUMULATOR, new Item.Settings().rarity(Rarity.RARE), Items.GLASS));

    private static Item register(String path, Item item) {
        IN_CREATIVE_INVENTORY.add(item);
        return registerNoCreativeTab(path, item);
    }

    private static Item registerNoCreativeTab(String path, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Zauber.MOD_ID, path), item);
    }

    public static void init() {

    }
}
