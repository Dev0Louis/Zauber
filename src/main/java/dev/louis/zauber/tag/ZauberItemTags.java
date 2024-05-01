package dev.louis.zauber.tag;

import dev.louis.zauber.Zauber;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ZauberItemTags {
    public static final TagKey<Item> MANA_CONTAINING = of("mana_containing");

    private static TagKey<Item> of(String id) {
        return TagKey.of(RegistryKeys.ITEM, Identifier.of(Zauber.MOD_ID, id));
    }
}
