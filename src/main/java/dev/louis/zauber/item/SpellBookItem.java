package dev.louis.zauber.item;

import dev.louis.nebula.api.spell.SpellType;
import dev.louis.zauber.Zauber;
import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.item.PolymerItemUtils;
import eu.pb4.polymer.core.api.utils.PolymerClientDecoded;
import eu.pb4.polymer.core.api.utils.PolymerKeepModel;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

;

public class SpellBookItem extends Item implements PolymerItem, PolymerKeepModel, PolymerClientDecoded {
    public SpellBookItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        ItemStack itemStack = playerEntity.getStackInHand(hand);
        if(world.isClient())return TypedActionResult.pass(itemStack);
        if(hand != Hand.MAIN_HAND)return TypedActionResult.pass(itemStack);

        Optional<SpellType<?>> optionalSpellType = getSpellType(itemStack);
        if(optionalSpellType.isPresent()) {
            playerEntity.getSpellManager().learnSpell(optionalSpellType.get());
            itemStack.decrement(1);
            return TypedActionResult.consume(itemStack);
        }
        return TypedActionResult.fail(itemStack);
    }

    /**
     * This converts to the new namespace if needed
     * @param nbt
     */
    @Override
    public void postProcessNbt(NbtCompound nbt) {
        var spellString = nbt.getString("spell");
        var spellId = Identifier.tryParse(spellString);
        nbt.putString("spell", spellId.toString());
    }

    public static Optional<SpellType<?>> getSpellType(ItemStack itemStack) {
        if(!itemStack.hasNbt() || itemStack.getNbt() == null)return Optional.empty();
        String spell = itemStack.getNbt().getString("spell");
        return SpellType.get(Identifier.tryParse(spell));
    }

    public static ItemStack createSpellBook(SpellType<?> spellType) {
        ItemStack itemStack = new ItemStack(ZauberItems.SPELL_BOOK);
        itemStack.getOrCreateNbt().putString("spell", spellType.getId().toString());
        return itemStack;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        if(Zauber.isClientModded(player)) return itemStack.getItem();
        return Items.BOOK;
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipContext context, @Nullable ServerPlayerEntity player) {
        if(Zauber.isClientModded(player)) return itemStack;
        return PolymerItemUtils.createItemStack(itemStack, context, player);
    }
}