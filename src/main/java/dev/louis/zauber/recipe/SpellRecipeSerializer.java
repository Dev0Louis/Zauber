package dev.louis.zauber.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.louis.zauber.Zauber;
import dev.louis.zauber.item.SpellBookItem;
import dev.louis.zauber.spell.type.SpellType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;

public class SpellRecipeSerializer implements RecipeSerializer<SpellRecipe> {
    public static final Identifier ID = Identifier.of(Zauber.MOD_ID, "spell_recipe");
    public static final SpellRecipeSerializer INSTANCE = new SpellRecipeSerializer();
    public static final PacketCodec<RegistryByteBuf, SpellRecipe> PACKET_CODEC = new PacketCodec<>() {
        @Override
        public SpellRecipe decode(RegistryByteBuf buf) {
            Ingredient ingredient = Ingredient.PACKET_CODEC.decode(buf);
            var spellType = SpellType.get(buf.readIdentifier()).orElseThrow();
            return new SpellRecipe(ingredient, SpellBookItem.createSpellBook(spellType));
        }

        @Override
        public void encode(RegistryByteBuf buf, SpellRecipe recipe) {
            var spellType = SpellBookItem.getSpellType(recipe.result()).orElseThrow();
            Ingredient.PACKET_CODEC.encode(buf, recipe.ingredient());
            buf.writeIdentifier(Identifier.tryParse(spellType.getIdAsString()));
        }
    };
    private final MapCodec<SpellRecipe> codec;


    private SpellRecipeSerializer() {
        this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Identifier.CODEC.fieldOf("spell").forGetter(SpellRecipe::getSpellId),
                Ingredient.CODEC.fieldOf("ingredient").forGetter(SpellRecipe::ingredient)
        ).apply(
                instance,
                (identifier, ingredient) -> new SpellRecipe(ingredient, SpellBookItem.createSpellBook(SpellType.get(identifier).orElseThrow()))
        ));
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
