package dev.louis.zauber.items;

import dev.louis.zauber.Zauber;
import dev.louis.zauber.block.ZauberBlocks;
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
    /*public static final Item RITUAL_STONE =
            Items.register(Identifier.of(Zauber.MOD_ID, "ritual_stone"), new BlockItem(ZauberBlocks.RITUAL_STONE, new Item.Settings()));
    public static final Item RITUAL_ITEM_SACRIFICER =
            Items.register(Identifier.of(Zauber.MOD_ID, "ritual_item_sacrificer"), new BlockItem(ZauberBlocks.ITEM_SACRIFICER, new Item.Settings()));*/

    public static void init() {

    }
}
