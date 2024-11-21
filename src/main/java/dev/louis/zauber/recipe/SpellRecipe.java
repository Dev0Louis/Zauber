package dev.louis.zauber.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBookCategories;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

public record SpellRecipe(Ingredient ingredient, ItemStack result) implements Recipe<RecipeInput> {

    public Identifier getSpellId() {
        return Registries.ITEM.getId(result().getItem());
    }

    @Override
    public boolean matches(RecipeInput recipeInput, World world) {
        return recipeInput.getStackInSlot(0).isOf(Items.BOOK) && ingredient.test(recipeInput.getStackInSlot(1));
    }

    @Override
    public ItemStack craft(RecipeInput inventory, RegistryWrapper.WrapperLookup lookup) {
        return this.result.copy();
    }

    @Override
    public RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer() {
        return SpellRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<? extends Recipe<RecipeInput>> getType() {
        return ZauberRecipes.SPELL_RECIPE;
    }

    @Override
    public IngredientPlacement getIngredientPlacement() {
        return IngredientPlacement.forSingleSlot(ingredient);
    }

    @Override
    public RecipeBookCategory getRecipeBookCategory() {
        return RecipeBookCategories.STONECUTTER;
    }
}
