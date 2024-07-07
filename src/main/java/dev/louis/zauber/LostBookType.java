package dev.louis.zauber;

import dev.louis.zauber.inventory.SimpleOwnableImmutableSingleStackInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.screen.LecternScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldEvents;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record LostBookType(Identifier id, List<Text> pages) implements NamedScreenHandlerFactory {
    public static List<LostBookType> LOST_BOOKS = new ArrayList<>();

    public static void init() {
        registerBook(Identifier.of(Zauber.MOD_ID, "dark_sleep"), 2);
        registerBook(Identifier.of(Zauber.MOD_ID, "mana_cauldron"), 1);
        registerBook(Identifier.of(Zauber.MOD_ID, "moral_implications"), 3);
        registerBook(Identifier.of(Zauber.MOD_ID, "a_poem"), 1);
        //registerBook(Identifier.of(Zauber.MOD_ID, "nickel"), 1);
        registerBook(Identifier.of(Zauber.MOD_ID, "knowledge_retention_spell"), 2);
        registerBook(Identifier.of(Zauber.MOD_ID, "smelting"), 3);
        registerBook(Identifier.of(Zauber.MOD_ID, "summoning"), 3);
        registerBook(Identifier.of(Zauber.MOD_ID, "heart_of_the_sea"), 3);
        registerBook(Identifier.of(Zauber.MOD_ID, "mana_horse"), 3);

    }

    public static Optional<LostBookType> getById(Identifier id) {
        return LOST_BOOKS.stream().filter(lostBookType -> lostBookType.id.equals(id)).findAny();
    }


    public static void registerBook(Identifier id, int pageCount) {
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
        if (!LOST_BOOKS.isEmpty()) {
            NbtList nbtList = new NbtList();
            this.pages().forEach(text -> nbtList.add(NbtString.of(text.getString())));
            itemStack.setSubNbt("pages", nbtList);
        }

        itemStack.setSubNbt("author", NbtString.of("Unknown Magician"));
        itemStack.setSubNbt("title", NbtString.of(this.id.getPath()));
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
