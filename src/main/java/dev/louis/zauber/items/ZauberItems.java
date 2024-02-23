package dev.louis.zauber.items;

import dev.louis.zauber.Zauber;
import dev.louis.zauber.blocks.ZauberBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ZauberItems {
    public static final Item SPELL_BOOK = Items.register(new Identifier(Zauber.MOD_ID, "spell_book"), new SpellBookItem(new Item.Settings().fireproof().rarity(Rarity.RARE)));
    public static final Item SOUL_HORN = Items.register(new Identifier(Zauber.MOD_ID, "soul_horn"), new SoulHornItem(new Item.Settings().rarity(Rarity.RARE)));
    public static final Item SPELL_TABLE = Items.register(new Identifier(Zauber.MOD_ID, "spell_table"), new SpellTableItem(ZauberBlocks.SPELL_TABLE, new Item.Settings().rarity(Rarity.EPIC)));
    public static void init() {
    }
}
