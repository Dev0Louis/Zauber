package dev.louis.zauber.mana.effect;

import dev.louis.zauber.Zauber;
import net.minecraft.entity.effect.InstantStatusEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ManaEffects {

    private static Potion MANA_INSTANT;
    private static Potion STRONG_MANA_INSTANT;
    private static Potion MANA_REGENERATION;
    private static Potion STRONG_MANA_REGENERATION;
    private static InstantStatusEffect MANA_INSTANT_EFFECT = new InstantManaStatusEffect();
    private static StatusEffect MANA_REGENERATION_EFFECT = new ManaRegenerationStatusEffect();


    public static void init() {
        registerPotionEffects();
        registerPotions();
        registerPotionRecipe();
    }

    private static void registerPotionEffects() {
        register("instant_mana", MANA_INSTANT_EFFECT);
        register("mana_regeneration", MANA_REGENERATION_EFFECT);
    }

    private static void registerPotions() {
        MANA_INSTANT = register("instant_mana", new Potion("instant_mana", new StatusEffectInstance(MANA_INSTANT_EFFECT, 1)));
        STRONG_MANA_INSTANT = register("strong_instant_mana", new Potion("strong_instant_mana", new StatusEffectInstance(MANA_INSTANT_EFFECT, 1, 1)));
        MANA_REGENERATION = register("mana_regeneration", new Potion("mana_regeneration", new StatusEffectInstance(MANA_REGENERATION_EFFECT, 5)));
        STRONG_MANA_REGENERATION = register("strong_mana_regeneration", new Potion("strong_mana_regeneration", new StatusEffectInstance(MANA_REGENERATION_EFFECT, 5, 1)));

    }

    private static void registerPotionRecipe() {
        BrewingRecipeRegistry.registerPotionRecipe(Potions.AWKWARD, Items.CHORUS_FLOWER, MANA_INSTANT);
        BrewingRecipeRegistry.registerPotionRecipe(MANA_INSTANT, Items.CHORUS_FLOWER, STRONG_MANA_INSTANT);
    }




    private static Potion register(String path, Potion potion) {
        return Registry.register(Registries.POTION, Identifier.of(Zauber.MOD_ID, path), potion);
    }



    private static StatusEffect register(String path, StatusEffect entry) {
        return Registry.register(Registries.STATUS_EFFECT, Identifier.of(Zauber.MOD_ID, path), entry);
    }
}
