package dev.louis.zauber.tag;

import dev.louis.zauber.Zauber;
import net.minecraft.potion.Potion;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ZauberPotionTags {
    public static final TagKey<Potion> MANA = of("mana");

    public static void init() {}

    private static TagKey<Potion> of(String id) {
        return TagKey.of(RegistryKeys.POTION, Identifier.of(Zauber.MOD_ID, id));
    }
}
