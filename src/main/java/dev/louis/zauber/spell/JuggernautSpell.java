package dev.louis.zauber.spell;

import com.google.common.collect.ImmutableList;
import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellType;
import dev.louis.zauber.config.ConfigManager;
import dev.louis.zauber.duck.ItemStackJuggernautModeDuck;
import dev.louis.zauber.mixin.ServerWorldAccessor;
import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

public class JuggernautSpell extends Spell {
    public JuggernautSpell(SpellType<?> spellType, PlayerEntity caster) {
        super(spellType, caster);
    }

    @Override
    public void cast() {
        getCaster().getInventory().dropAll();

        var tick = ((ServerWorldAccessor)getCaster().getWorld()).getWorldProperties().getTime();
        var player = (ServerPlayerEntity) getCaster();
        generateJuggernautItemAndSetToSlot(player, 0, Items.NETHERITE_SWORD, tick);
        generateJuggernautItemAndSetToSlot(player, 1, Items.MACE, tick);
        generateJuggernautItemAndSetToSlot(player, 2, Items.NETHERITE_AXE, tick);
        generateJuggernautItemAndSetToSlot(player, 3, Items.BOW, tick);

        RegistryWrapper<Enchantment> registry = player.getServer().getRegistryManager().getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
        ItemStack goldenApple = generateJuggernautItem(Items.GOLDEN_APPLE, registry, tick);
        goldenApple.setCount(26);
        player.getInventory().setStack(3, goldenApple);


        ItemStack arrow = generateJuggernautItem(Items.ARROW, registry, tick);
        arrow.setCount(1);
        player.getInventory().setStack(10, arrow);

        player.getInventory().armor.set(0, generateJuggernautItem(Items.NETHERITE_BOOTS, registry, tick));
        player.getInventory().armor.set(1, generateJuggernautItem(Items.NETHERITE_LEGGINGS, registry, tick));
        player.getInventory().armor.set(2, generateJuggernautItem(Items.NETHERITE_CHESTPLATE, registry, tick));
        player.getInventory().armor.set(3, generateJuggernautItem(Items.NETHERITE_HELMET, registry, tick));

    }
    private void playWarningSound() {
        this.getCaster().getWorld().playSound(null, getCaster().getBlockPos(), SoundEvents.BLOCK_NOTE_BLOCK_BELL.value(), SoundCategory.PLAYERS);
    }

    @Override
    public void tick() {
        if(age % 10 == 0) {
            this.getCaster().getManaManager().addMana(1);
        }
        if(age % 40 == 0) {
            if(age > 2200) {
                playWarningSound();
            }
        }
        if(age > 2350) {
            if(age % 5 == 0) {
                playWarningSound();
            }
        }
    }

    @Override
    public int getDuration() {
        return ConfigManager.getServerConfig().juggernautSpellDuration();
    }

    @Override
    public void finish() {
        JuggernautSpell.clearJuggernautItems((ServerPlayerEntity) getCaster());
        if(getCaster().isAlive()) {
            getCaster().damage(getCaster().getDamageSources().magic(), 100f);
            getCaster().setHealth(0);
        }
    }

    public static void generateJuggernautItemAndSetToSlot(ServerPlayerEntity player, int slot, Item item, long tick) {
        player.getInventory().setStack(slot, generateJuggernautItem(item, player.getWorld().getServer().getRegistryManager().getWrapperOrThrow(RegistryKeys.ENCHANTMENT), tick));

    }


    public static ItemStack generateJuggernautItem(Item item, RegistryWrapper<Enchantment> registry, long tickWorldtime) {
        return generateJuggernautItem(new ItemStack(item), registry, tickWorldtime);
    }

    public static ItemStack generateJuggernautItem(ItemStack itemStack, RegistryWrapper<Enchantment> registry, long tickWorldtime) {
        if(itemStack.isEnchantable()) {
            enchantMax(itemStack, registry);
        }
        ItemStackJuggernautModeDuck.access(itemStack).zauber$setJuggernautModeTick(tickWorldtime);
        return itemStack;
    }

    public static void enchantMax(ItemStack itemStack, RegistryWrapper<Enchantment> registry) {
        enchantMax(itemStack, registry, List.of(Enchantments.BANE_OF_ARTHROPODS, Enchantments.SMITE, Enchantments.KNOCKBACK, Enchantments.MENDING, Enchantments.FROST_WALKER, Enchantments.FIRE_PROTECTION, Enchantments.PROJECTILE_PROTECTION, Enchantments.BLAST_PROTECTION));
    }

    public static void enchantMax(ItemStack itemStack, RegistryWrapper<Enchantment> registry, List<RegistryKey<Enchantment>> excludedEnchantments) {
        registry.streamKeys().filter(enchantmentKey -> !excludedEnchantments.contains(enchantmentKey)).forEach(enchantmentKey -> {
            var enchantment = registry.getOrThrow(enchantmentKey);
            if (itemStack.canBeEnchantedWith(enchantment, EnchantingContext.ACCEPTABLE)) {
                itemStack.addEnchantment(enchantment, enchantment.value().getMaxLevel() + 1);
            }
        });
    }


    public static void clearJuggernautItems(ServerPlayerEntity player) {
        List<DefaultedList<ItemStack>> combinedInventory = ImmutableList.of(player.getInventory().main, player.getInventory().armor, player.getInventory().offHand);
        for (List<ItemStack> list : combinedInventory) {
            for (int i = 0; i < list.size(); ++i) {
                ItemStack itemStack = list.get(i);

                if (itemStack.isEmpty()) continue;
                if (ItemStackJuggernautModeDuck.access(itemStack).zauber$getJuggernautTick() <= 0L)continue;
                list.set(i, ItemStack.EMPTY);
            }
        }
    }
}
