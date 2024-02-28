package dev.louis.zauber.recipe;

import dev.louis.zauber.Zauber;
import dev.louis.zauber.screen.PolymerScreenHandlerType;
import dev.louis.zauber.screen.SpellTableScreenHandler;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ZauberRecipes {
    public static final RecipeType<SpellRecipe> SPELL_RECIPE = RecipeType.register(Zauber.MOD_ID + ":spell_recipe");
    public static final ScreenHandlerType<SpellTableScreenHandler> SPELL_TABLE = register(new Identifier(Zauber.MOD_ID, "spellcraft"), SpellTableScreenHandler::new);
    public static void init() {
        Registry.register(Registries.RECIPE_SERIALIZER, SpellRecipe.SpellRecipeSerializer.ID, SpellRecipe.SpellRecipeSerializer.INSTANCE);
    }

    public static void initClient() {
    }

    private static <T extends ScreenHandler> ScreenHandlerType<T> register(Identifier id, ScreenHandlerType.Factory<T> factory) {
        return Registry.register(Registries.SCREEN_HANDLER, id, new PolymerScreenHandlerType<>(factory, FeatureFlags.VANILLA_FEATURES, ScreenHandlerType.STONECUTTER));
    }
}
