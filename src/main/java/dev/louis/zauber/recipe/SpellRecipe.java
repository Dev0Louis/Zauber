package dev.louis.zauber.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.louis.zauber.spell.type.SpellType;

import dev.louis.zauber.Zauber;
import dev.louis.zauber.item.SpellBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public record SpellRecipe(Ingredient ingredient, ItemStack result) implements Recipe<RecipeInput> {
    public static final SpellRecipe EMPTY = new SpellRecipe(Ingredient.empty(), ItemStack.EMPTY);

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup lookup) {
        return result();
    }

    public Identifier getSpellId() {
        return Registries.ITEM.getId(result().getItem());
    }

    @Override
    public boolean matches(RecipeInput recipeInput, World world) {
        return recipeInput.getStackInSlot(0).isOf(Items.BOOK) && ingredient.test(recipeInput.getStackInSlot(1));
    }

    @Override
    public ItemStack craft(RecipeInput inventory, RegistryWrapper.WrapperLookup lookup) {
        return this.getResult(lookup).copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SpellRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return ZauberRecipes.SPELL_RECIPE;
    }

    public static class SpellRecipeSerializer implements RecipeSerializer<SpellRecipe> {
        public static final Identifier ID = Identifier.of(Zauber.MOD_ID, "spell_recipe");
        public static final SpellRecipeSerializer INSTANCE = new SpellRecipeSerializer();
        public static final PacketCodec<RegistryByteBuf, SpellRecipe> PACKET_CODEC = new PacketCodec<>() {
            @Override
            public SpellRecipe decode(RegistryByteBuf buf) {
                if (!buf.readBoolean()) return SpellRecipe.EMPTY;

                Ingredient ingredient;
                if (buf.readBoolean()) ingredient = Ingredient.PACKET_CODEC.decode(buf);
                else ingredient = Ingredient.empty();

                var spellType = SpellType.get(buf.readIdentifier());
                return spellType.map(type -> new SpellRecipe(ingredient, SpellBookItem.createSpellBook(type))).orElse(SpellRecipe.EMPTY);
            }

            @Override
            public void encode(RegistryByteBuf buf, SpellRecipe recipe) {
                var spellTypeOptional = SpellBookItem.getSpellType(recipe.result);
                buf.writeBoolean(spellTypeOptional.isPresent());

                spellTypeOptional.ifPresent(spellType -> {
                    buf.writeBoolean(!recipe.ingredient.isEmpty());
                    if (!recipe.ingredient.isEmpty()) {
                        Ingredient.PACKET_CODEC.encode(buf, recipe.ingredient);
                    }

                    buf.writeIdentifier(Identifier.tryParse(spellType.getIdAsString()));
                });
            }
        };
        private final MapCodec<SpellRecipe> codec;


        private SpellRecipeSerializer() {
            this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Identifier.CODEC.fieldOf("spell").forGetter(SpellRecipe::getSpellId),
                    Ingredient.DISALLOW_EMPTY_CODEC.optionalFieldOf("ingredient", Ingredient.empty()).forGetter(SpellRecipe::ingredient)
            ).apply(instance, (identifier, ingredient) -> SpellType.get(identifier).map(type -> new SpellRecipe(ingredient, SpellBookItem.createSpellBook(type))).orElse(SpellRecipe.EMPTY)));
        }

        @Override
        public MapCodec<SpellRecipe> codec() {
            return codec;
        }

        @Override
        public PacketCodec<RegistryByteBuf, SpellRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
}
