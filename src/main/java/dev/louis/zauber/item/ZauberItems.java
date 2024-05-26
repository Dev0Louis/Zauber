package dev.louis.zauber.item;

import dev.louis.zauber.Zauber;
import dev.louis.zauber.block.ZauberBlocks;
import eu.pb4.polymer.core.api.item.PolymerBlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ZauberItems {
    public static final Item SPELL_BOOK =
            Items.register(Identifier.of(Zauber.MOD_ID, "spell_book"), new SpellBookItem(new Item.Settings().fireproof().rarity(Rarity.RARE)));
    public static final Item SOUL_HORN =
            Items.register(Identifier.of(Zauber.MOD_ID, "soul_horn"), new SoulHornItem(new Item.Settings().rarity(Rarity.RARE)));
    public static final Item SPELL_TABLE =
            Items.register(Identifier.of(Zauber.MOD_ID, "spell_table"), new SpellTableItem(ZauberBlocks.SPELL_TABLE, new Item.Settings().rarity(Rarity.EPIC)));
    public static final Item HEART_OF_THE_DARKNESS =
            Items.register(Identifier.of(Zauber.MOD_ID, "heart_of_the_darkness"), new HeartOfTheDarknessItem(new Item.Settings().rarity(Rarity.UNCOMMON)));
    public static final Item RITUAL_STONE =
            Items.register(Identifier.of(Zauber.MOD_ID, "ritual_stone"), new PolymerBlockItem(ZauberBlocks.RITUAL_STONE, new Item.Settings().rarity(Rarity.RARE), Items.LODESTONE));
    public static final Item ITEM_SACRIFICER =
            Items.register(Identifier.of(Zauber.MOD_ID, "item_sacrificer"), new PolymerBlockItem(ZauberBlocks.ITEM_SACRIFICER, new Item.Settings().rarity(Rarity.RARE), Items.STONE_BRICK_WALL));
    public static final Item DARKNESS_ACCUMULATOR =
            Items.register(Identifier.of(Zauber.MOD_ID, "darkness_accumulator"), new PolymerBlockItem(ZauberBlocks.DARKNESS_ACCUMULATOR, new Item.Settings().rarity(Rarity.RARE), Items.GLASS));
    //public static final Item TRAPPED_BED =
    //        Items.register(Identifier.of(Zauber.MOD_ID, "trapped_bed"), new BedItem(ZauberBlocks.TRAPPING_BED, new Item.Settings()));

    public static void init() {

    }
}
