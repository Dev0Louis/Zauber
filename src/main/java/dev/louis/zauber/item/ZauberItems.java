package dev.louis.zauber.item;

import dev.louis.zauber.Zauber;
import dev.louis.zauber.block.ZauberBlocks;
import eu.pb4.polymer.core.api.item.PolymerBlockItem;
import eu.pb4.polymer.rsm.api.RegistrySyncUtils;
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

    //It is important that these are above, as the Blocks will be initialised if any BlockItems are registered before these and the Cauldron behavior needs to be put into a map, if this map is created before these items are initialised resulting in null -> Behavior, which breaks the interaction
    public static final Item TOTEM_OF_DARKNESS =
            registerCreativeTab("totem_of_darkness", new TotemOfDarknessItem(new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)));
    public static final Item TOTEM_OF_ICE =
            registerCreativeTab("totem_of_ice", new TotemOfIceItem(new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)));
    public static final Item TOTEM_OF_MANA =
            registerCreativeTab("totem_of_mana", new TotemOfManaItem(new Item.Settings().maxDamage(100).rarity(Rarity.UNCOMMON)));

    public static final Item SPELL_BOOK =
            register("spell_book", new SpellBookItem(new Item.Settings().fireproof().rarity(Rarity.RARE)));
    public static final Item SOUL_HORN =
            register("soul_horn", new SoulHornItem(new Item.Settings().rarity(Rarity.RARE)));
    public static final Item SPELL_TABLE =
            registerCreativeTab("spell_table", new SpellTableItem(ZauberBlocks.SPELL_TABLE, new Item.Settings().rarity(Rarity.EPIC)));
    public static final Item HEART_OF_THE_ICE =
            registerCreativeTab("heart_of_the_ice", new HeartOfTheIceItem(new Item.Settings().rarity(Rarity.UNCOMMON)));
    public static final Item HEART_OF_THE_DARKNESS =
            registerCreativeTab("heart_of_the_darkness", new HeartOfTheDarknessItem(new Item.Settings().rarity(Rarity.UNCOMMON)));
    public static final Item RITUAL_STONE =
            registerCreativeTab("ritual_stone", new PolymerBlockItem(ZauberBlocks.RITUAL_STONE, new Item.Settings().rarity(Rarity.RARE), Items.LODESTONE));
    public static final Item ITEM_SACRIFICER =
            registerCreativeTab("item_sacrificer", new PolymerBlockItem(ZauberBlocks.ITEM_SACRIFICER, new Item.Settings().rarity(Rarity.RARE), Items.STONE_BRICK_WALL));
    public static final Item DARKNESS_ACCUMULATOR =
            registerCreativeTab("darkness_accumulator", new PolymerBlockItem(ZauberBlocks.DARKNESS_ACCUMULATOR, new Item.Settings().rarity(Rarity.RARE), Items.GLASS));
    public static final Item MANA_BOW =
            registerCreativeTab("mana_bow", new ManaBowItem(new Item.Settings().rarity(Rarity.UNCOMMON).maxDamage(384)));
    public static final Item LOST_BOOK =
            register("lost_book", new LostBookItem(new Item.Settings().rarity(Rarity.COMMON)));


    private static Item registerCreativeTab(String path, Item item) {
        IN_CREATIVE_INVENTORY.add(item);
        return register(path, item);
    }

    private static Item register(String path, Item item) {
        RegistrySyncUtils.setServerEntry(Registries.ITEM, item);
        return Registry.register(Registries.ITEM, Identifier.of(Zauber.MOD_ID, path), item);
    }

    public static void init() {

    }
}
