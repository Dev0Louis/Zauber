package dev.louis.zauber;

import dev.louis.zauber.inventory.SimpleOwnableImmutableSingleStackInventory;
import dev.louis.zauber.ripped_page.RippedPage;
import dev.louis.zauber.ripped_page.RippedPages;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.*;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldEvents;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record LostBookType(Identifier id, List<Text> pages) implements NamedScreenHandlerFactory {
    public static List<LostBookType> LOST_BOOKS = new ArrayList<>();
    public static Style LOST_BOOK_STYLE = Style.EMPTY.withFont(Identifier.of(Zauber.MOD_ID, "ripped_page")).withColor(0xFFFFFF);

    public static void init() {
        registerBook(Identifier.of(Zauber.MOD_ID, "dark_sleep"), 2, RippedPages.EMPTY);
        //registerBook(Identifier.of(Zauber.MOD_ID, "mana_cauldron"), 1);
        //registerBook(Identifier.of(Zauber.MOD_ID, "moral_implications"), 3);
        //registerBook(Identifier.of(Zauber.MOD_ID, "a_poem"), 1);
        //registerBook(Identifier.of(Zauber.MOD_ID, "knowledge_retention_spell"), 2);
        //registerBook(Identifier.of(Zauber.MOD_ID, "smelting"), 3);
        //registerBook(Identifier.of(Zauber.MOD_ID, "summoning"), 3);
        //registerBook(Identifier.of(Zauber.MOD_ID, "heart_of_the_sea"), 3);
        //registerBook(Identifier.of(Zauber.MOD_ID, "mana_horse"), 3);

        /*Style style = Style.EMPTY.withFont(Identifier.of(Zauber.MOD_ID, "circle")).withColor(0xFFFFFF);
        Style style1 = Style.EMPTY.withFont(Identifier.of(Zauber.MOD_ID, "ritual")).withColor(0xFFFFFF);

        var circle = Text.literal(" \u0041").setStyle(style);
        var manaCauldron = Text.literal("   \u0041").setStyle(style1);
        var ritualStone = Text.literal("         \u0043").setStyle(style1);
        registerBook(Identifier.of(Zauber.MOD_ID, "mana_bow"), circle
                .append("\n\n")
                .append(manaCauldron)
                .append("\n\n\n\n\n")
                .append(ritualStone)
        );*/
        /*Style style = Style.EMPTY.withFont();
        Style style1 = style.withColor(0xFFFFFF);
        MutableText text = Text.literal("\u0042").setStyle(style1);
        MutableText text1 = Text.literal("\u0041").setStyle(style);
        MutableText text2 = Text.literal("ABCDEFGHI").setStyle(Style.EMPTY.withColor(0xFF334F).withFont(Style.DEFAULT_FONT_ID));
        MutableText text3 = Text.literal("\u0043").setStyle(style);
        // MutableText text3 = Text.literal("").setStyle(Style.EMPTY.withColor(0xFF334F).withFont(Style.DEFAULT_FONT_ID));

        registerBook(
                Identifier.of(Zauber.MOD_ID, "test"),
                text.append(text1).append("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n").append(text3).append(text1)
        );*/

    }

    public static Optional<LostBookType> getById(Identifier id) {
        return LOST_BOOKS.stream().filter(lostBookType -> lostBookType.id.equals(id)).findAny();
    }


    public static void registerBook(Identifier id, int pageCount, RippedPage rippedPage) {
        if (LOST_BOOKS.stream().anyMatch(lostBookType -> id.equals(lostBookType.id))) {
            throw new IllegalStateException("Tried registering " + id + " while that id was already registered.");
        }
        List<Text> pages = new ArrayList<>();

        for (int i = 0; i < pageCount; i++) {
            pages.add(Text.translatable("lost_book." + id.getNamespace() + "." + id.getPath() + "." + i));
        }


        LOST_BOOKS.add(new LostBookType(id, pages));
    }


    public static void registerBook(Identifier id, Text... pages) {
        if (LOST_BOOKS.stream().anyMatch(lostBookType -> id.equals(lostBookType.id))) {
            throw new IllegalStateException("Tried registering " + id + " while that id was already registered.");
        }
        LOST_BOOKS.add(new LostBookType(id, List.of(pages)));
    }

    public static LostBookType getRandom(Random random) {
        return LOST_BOOKS.get(random.nextInt(LOST_BOOKS.size()));
    }

    private ItemStack createFakeBook() {
        ItemStack itemStack = Items.WRITTEN_BOOK.getDefaultStack();

        itemStack.set(
                DataComponentTypes.WRITTEN_BOOK_CONTENT,
                new WrittenBookContentComponent(
                        RawFilteredPair.of(this.id.getPath()),
                        "Unknown Magician",
                        3,
                        this.pages().stream().map(RawFilteredPair::of).toList(),
                        false
                )
        );

        return itemStack;
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new LecternScreenHandler(syncId, new SimpleOwnableImmutableSingleStackInventory(player, createFakeBook()), new PropertyDelegate() {
            private int page;

            @Override
            public int get(int index) {
                return index == 0 ? page : 0;
            }

            @Override
            public void set(int index, int value) {
                if (index == 0) {
                    player.getWorld().syncWorldEvent(WorldEvents.LECTERN_BOOK_PAGE_TURNED, player.getBlockPos(), 0);
                    page = value;
                }
            }

            @Override
            public int size() {
                return 1;
            }
        }) {

            @Override
            public boolean onButtonClick(PlayerEntity player, int id) {
                if (id == 3) return false;
                return super.onButtonClick(player, id);
            }
        };
    }

    @Override
    public Text getDisplayName() {
        return Text.of(id);
    }
}
