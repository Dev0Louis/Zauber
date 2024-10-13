package dev.louis.zauber.item;

import dev.louis.zauber.component.item.ZauberDataComponentTypes;
import dev.louis.zauber.component.item.type.StoredSpellComponent;
import dev.louis.zauber.spell.type.SpellType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.Optional;


public class SpellBookItem extends Item {
    public SpellBookItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        ItemStack itemStack = playerEntity.getStackInHand(hand);
        if (world.isClient()) return TypedActionResult.pass(itemStack);
        if (hand != Hand.MAIN_HAND) return TypedActionResult.pass(itemStack);

        Optional<RegistryEntry<SpellType<?>>> optionalSpellType = getSpellType(itemStack);
        if (optionalSpellType.isPresent()) {
            //TODO: Need to reconsider
            //playerEntity.getSpellManager().learnSpell(optionalSpellType.get().value());
            itemStack.decrement(1);
            return TypedActionResult.consume(itemStack);
        }
        return TypedActionResult.fail(itemStack);
    }

    public static Optional<RegistryEntry<SpellType<?>>> getSpellType(ItemStack itemStack) {
        StoredSpellComponent storedSpell = itemStack.get(ZauberDataComponentTypes.STORED_SPELL_TYPE);
        if (storedSpell == null) return Optional.empty();
        return Optional.of(storedSpell.spellType());
    }

    public static ItemStack createSpellBook(SpellType<?> spellType) {
        ItemStack itemStack = new ItemStack(ZauberItems.SPELL_BOOK);
        itemStack.set(ZauberDataComponentTypes.STORED_SPELL_TYPE, new StoredSpellComponent(SpellType.REGISTRY.getEntry(spellType)));
        return itemStack;
    }
}