package dev.louis.zauber.items;

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
    public static final Item RITUAL_STONE =
            Items.register(Identifier.of(Zauber.MOD_ID, "ritual_stone"), new PolymerBlockItem(ZauberBlocks.RITUAL_STONE, new Item.Settings(), Items.LODESTONE));
    public static final Item ITEM_SACRIFICER =
            Items.register(Identifier.of(Zauber.MOD_ID, "item_sacrificer"), new PolymerBlockItem(ZauberBlocks.ITEM_SACRIFICER, new Item.Settings(), Items.RESPAWN_ANCHOR));
    public static final Item MANA_STORAGE =
            Items.register(Identifier.of(Zauber.MOD_ID, "mana_storage"), new PolymerBlockItem(ZauberBlocks.MANA_STORAGE, new Item.Settings(), Items.BLUE_STAINED_GLASS));
    public static final Item HORSE_RITUAL_SCROLL =
            Items.register(Identifier.of(Zauber.MOD_ID, "horse_ritual_scroll"), new HorseRitualScrollItem(new Item.Settings()));

    public static void init() {

    }
}
