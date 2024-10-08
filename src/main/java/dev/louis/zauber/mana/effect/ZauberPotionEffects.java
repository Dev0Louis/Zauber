package dev.louis.zauber.mana.effect;

import dev.louis.zauber.Zauber;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class ZauberPotionEffects {

    public static RegistryEntry.Reference<Potion> MANA_INSTANT;
    public static RegistryEntry.Reference<Potion> STRONG_MANA_INSTANT;
    public static RegistryEntry.Reference<Potion> MANA_REGENERATION;
    public static RegistryEntry.Reference<Potion> STRONG_MANA_REGENERATION;
    public static RegistryEntry.Reference<StatusEffect> MANA_INSTANT_EFFECT;
    public static RegistryEntry.Reference<StatusEffect> MANA_REGENERATION_EFFECT;


    public static void init() {
        registerPotionEffects();
        registerPotions();
        registerPotionRecipe();
    }

    private static void registerPotionEffects() {
        MANA_INSTANT_EFFECT = register("instant_mana", new InstantManaStatusEffect());
        MANA_REGENERATION_EFFECT = register("mana_regeneration", new ManaRegenerationStatusEffect());
    }

    private static void registerPotions() {
        MANA_INSTANT = register("instant_mana", new Potion("instant_mana", new StatusEffectInstance(MANA_INSTANT_EFFECT, 1)));
        STRONG_MANA_INSTANT = register("strong_instant_mana", new Potion("strong_instant_mana", new StatusEffectInstance(MANA_INSTANT_EFFECT, 1, 1)));
        MANA_REGENERATION = register("mana_regeneration", new Potion("mana_regeneration", new StatusEffectInstance(MANA_REGENERATION_EFFECT, 5)));
        STRONG_MANA_REGENERATION = register("strong_mana_regeneration", new Potion("strong_mana_regeneration", new StatusEffectInstance(MANA_REGENERATION_EFFECT, 5, 1)));

    }

    private static void registerPotionRecipe() {
        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
            builder.registerPotionRecipe(Potions.AWKWARD, Items.CHORUS_FLOWER, MANA_INSTANT);
            builder.registerPotionRecipe(MANA_INSTANT, Items.CHORUS_FLOWER, STRONG_MANA_INSTANT);
        });
    }


    private static RegistryEntry.Reference<Potion> register(String path, Potion potion) {
        return Registry.registerReference(Registries.POTION, Identifier.of(Zauber.MOD_ID, path), potion);
    }


    private static RegistryEntry.Reference<StatusEffect> register(String path, StatusEffect entry) {
        return Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(Zauber.MOD_ID, path), entry);
    }
}
