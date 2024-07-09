package dev.louis.zauber.item;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import dev.louis.zauber.Zauber;
import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.utils.PolymerClientDecoded;
import eu.pb4.polymer.core.api.utils.PolymerKeepModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class TotemOfDarknessItem extends TrinketItem implements PolymerItem, PolymerKeepModel, PolymerClientDecoded {
    public static final EntityAttributeModifier HALF =
            new EntityAttributeModifier(Identifier.of(Zauber.MOD_ID, "half"), -.5, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    public static final EntityAttributeModifier DOUBLE =
            new EntityAttributeModifier(Identifier.of(Zauber.MOD_ID, "double"), 1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    public static Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> activeMap = Multimaps.newMultimap(Maps.newLinkedHashMap(), ArrayList::new);

    static {
        activeMap.put(EntityAttributes.GENERIC_MAX_HEALTH, HALF);
        activeMap.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, DOUBLE);
    }

    public TotemOfDarknessItem(Settings settings) {
        super(settings);
    }

    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return Zauber.isClientModded(player) ? itemStack.getItem() : Items.TOTEM_OF_UNDYING;
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipType tooltipType, RegistryWrapper.WrapperLookup lookup, @Nullable ServerPlayerEntity player) {
        return Zauber.isClientModded(player) ? itemStack : PolymerItem.super.getPolymerItemStack(itemStack, tooltipType, lookup, player);
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (entity.getWorld().isClient()) return;

        if (isActive(entity)) {
            entity.getAttributes().addTemporaryModifiers(this.getTotemModifiers());
        }
    }


    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (entity.getWorld().isClient()) return;

        if (isActive(entity)) {
            entity.getAttributes().addTemporaryModifiers(this.getTotemModifiers());
        } else {
            entity.getAttributes().removeModifiers(this.getTotemModifiers());
        }
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (entity.getWorld().isClient()) return;

        entity.getAttributes().removeModifiers(this.getTotemModifiers());
    }


    public Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> getTotemModifiers() {
        return activeMap;
    }

    public static boolean isActive(LivingEntity entity) {
        return TrinketsApi.getTrinketComponent(entity).map(component -> component.isEquipped(ZauberItems.TOTEM_OF_DARKNESS)).orElse(false) && entity.getWorld().getLightLevel(entity.getBlockPos()) <= HeartOfTheDarknessItem.MAX_BRIGHTNESS;
    }
}
