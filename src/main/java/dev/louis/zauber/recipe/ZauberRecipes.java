package dev.louis.zauber.recipe;

import dev.louis.zauber.Zauber;
import dev.louis.zauber.screen.SpellTableScreenHandler;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategories;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ZauberRecipes {
    public static final RecipeType<SpellRecipe> SPELL_RECIPE = registerRecipeType("spell_recipe");
    public static final ScreenHandlerType<SpellTableScreenHandler> SPELL_TABLE = registerScreenHandler(Identifier.of(Zauber.MOD_ID, "spellcraft"), SpellTableScreenHandler::new);
    public static final RecipeBookCategory SPELL_BOOK_CATEGORY = RecipeBookCategories.register("spell_recipe");

    public static void init() {
        Registry.register(Registries.RECIPE_SERIALIZER, SpellRecipeSerializer.ID, SpellRecipeSerializer.INSTANCE);
    }

    static <T extends Recipe<?>> RecipeType<T> registerRecipeType(String id) {
        return Registry.register(Registries.RECIPE_TYPE, Identifier.of(Zauber.MOD_ID, id), new RecipeType<T>() {
            public String toString() {
                return id;
            }
        });
    }

    private static <T extends ScreenHandler> ScreenHandlerType<T> registerScreenHandler(Identifier id, ScreenHandlerType.Factory<T> factory) {
        return Registry.register(Registries.SCREEN_HANDLER, id, new ScreenHandlerType<>(factory, FeatureFlags.VANILLA_FEATURES));
    }

    public static RecipeBookCategory registerRecipeBookCategory(String id) {
        return Registry.register(Registries.RECIPE_BOOK_CATEGORY, Identifier.of(Zauber.MOD_ID, id), new RecipeBookCategory());
    }
}
