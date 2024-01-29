package dev.louis.zauber.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.louis.zauber.Zauber;
import dev.louis.zauber.items.SpellBookItem;
import dev.louis.nebula.api.spell.SpellType;;
import eu.pb4.polymer.core.api.item.PolymerRecipe;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public record SpellRecipe(ItemStack result) implements Recipe<Inventory>, PolymerRecipe {
    public static final SpellRecipe EMPTY = new SpellRecipe(new ItemStack(Items.AIR));

    @Override
    public ItemStack getResult(DynamicRegistryManager registryManager) {
        return result();
    }

    public Identifier getSpellId() {
        return Registries.ITEM.getId(result().getItem());
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        return inventory.getStack(0).isOf(Items.BOOK);
    }

    @Override
    public ItemStack craft(Inventory inventory, DynamicRegistryManager registryManager) {
        return this.getResult(registryManager).copy();
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
        return ModRecipes.SPELL_RECIPE;
    }

    public static class SpellRecipeSerializer implements RecipeSerializer<SpellRecipe> {
        public static final Identifier ID = new Identifier(Zauber.MOD_ID,"spell_recipe");
        public static final SpellRecipeSerializer INSTANCE = new SpellRecipeSerializer();
        private final Codec<SpellRecipe> codec;


        private SpellRecipeSerializer() {
            this.codec = RecordCodecBuilder.create(
                    instance -> instance.group(Identifier.CODEC.fieldOf("spell").forGetter(SpellRecipe::getSpellId))
                            .apply(
                                    instance,
                                    identifier -> SpellType.get(identifier).map(type -> new SpellRecipe(SpellBookItem.createSpellBook(type))).orElse(SpellRecipe.EMPTY)
                            )
            );
        }

        @Override
        public Codec<SpellRecipe> codec() {
            return codec;
        }

        @Override
        public SpellRecipe read(PacketByteBuf buf) {
            if(!buf.readBoolean())return SpellRecipe.EMPTY;
            var spellType = SpellType.get(buf.readIdentifier());
            return spellType.map(type -> new SpellRecipe(SpellBookItem.createSpellBook(type))).orElse(SpellRecipe.EMPTY);
        }

        @Override
        public void write(PacketByteBuf buf, SpellRecipe recipe) {
            var spellTypeOptional = SpellBookItem.getSpellType(recipe.result);
            buf.writeBoolean(spellTypeOptional.isPresent());
            spellTypeOptional.ifPresent(spellType -> buf.writeIdentifier(spellType.getId()));
        }
    }

    public Recipe<?> getPolymerReplacement(ServerPlayerEntity player) {
        if (Zauber.isClientVanilla(player)) return PolymerRecipe.createStonecuttingRecipe(this);
        return this;
    }
}
