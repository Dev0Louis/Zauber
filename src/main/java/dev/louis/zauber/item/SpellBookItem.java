package dev.louis.zauber.item;

import dev.louis.nebula.api.spell.SpellType;
import dev.louis.zauber.Zauber;
import dev.louis.zauber.component.ZauberDataComponentTypes;
import dev.louis.zauber.component.type.StoredSpellComponent;
import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.item.PolymerItemUtils;
import eu.pb4.polymer.core.api.utils.PolymerClientDecoded;
import eu.pb4.polymer.core.api.utils.PolymerKeepModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;


public class SpellBookItem extends Item implements PolymerItem, PolymerKeepModel, PolymerClientDecoded {
    public SpellBookItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        ItemStack itemStack = playerEntity.getStackInHand(hand);
        if(world.isClient())return TypedActionResult.pass(itemStack);
        if(hand != Hand.MAIN_HAND)return TypedActionResult.pass(itemStack);

        Optional<RegistryEntry<SpellType<?>>> optionalSpellType = getSpellType(itemStack);
        if(optionalSpellType.isPresent()) {
            playerEntity.getSpellManager().learnSpell(optionalSpellType.get().value());
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

    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        if(Zauber.isClientModded(player)) return itemStack.getItem();
        return Items.BOOK;
    }


    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipType tooltipType, RegistryWrapper.WrapperLookup lookup, @Nullable ServerPlayerEntity player) {
        if(Zauber.isClientModded(player)) return itemStack;
        return PolymerItemUtils.createItemStack(itemStack, lookup, player);
    }
}