package dev.louis.zauber.spell;

import com.google.common.collect.ImmutableList;
import dev.louis.zauber.accessor.ItemStackJuggernautModeAccessor;
import dev.louis.zauber.config.ZauberConfig;
import dev.louis.zauber.mixin.ServerWorldAccessor;
import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellType;;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.List;

public class JuggernautSpell extends Spell {

    public JuggernautSpell(SpellType<? extends Spell> spellType) {
        super(spellType);
    }

    @Override
    public void cast() {
        getCaster().getInventory().dropAll();

        var tick = ((ServerWorldAccessor)getCaster().getWorld()).getWorldProperties().getTime();
        var player = (ServerPlayerEntity) getCaster();
        generateJuggernautItemAndSetToSlot(player, 0, Items.NETHERITE_SWORD, tick);
        generateJuggernautItemAndSetToSlot(player, 1, Items.NETHERITE_AXE, tick);
        generateJuggernautItemAndSetToSlot(player, 2, Items.BOW, tick);

        ItemStack golden_apple = generateJuggernautItem(Items.GOLDEN_APPLE, tick);
        golden_apple.setCount(26);
        player.getInventory().setStack(3, golden_apple);


        ItemStack arrow = generateJuggernautItem(Items.ARROW, tick);
        arrow.setCount(1);
        player.getInventory().setStack(10, arrow);

        player.getInventory().armor.set(0, generateJuggernautItem(Items.NETHERITE_BOOTS, tick));
        player.getInventory().armor.set(1, generateJuggernautItem(Items.NETHERITE_LEGGINGS, tick));
        player.getInventory().armor.set(2, generateJuggernautItem(Items.NETHERITE_CHESTPLATE, tick));
        player.getInventory().armor.set(3, generateJuggernautItem(Items.NETHERITE_HELMET, tick));

    }
    private void playWarningSound() {
        this.getCaster().getWorld().playSound(null, getCaster().getBlockPos(), SoundEvents.BLOCK_NOTE_BLOCK_BELL.value(), SoundCategory.PLAYERS);
    }

    @Override
    public void tick() {
        if(spellAge % 10 == 0) {
            this.getCaster().getManaManager().addMana(1);
        }
        if(spellAge % 40 == 0) {
            if(spellAge > 2200) {
                playWarningSound();
            }
        }
        if(spellAge > 2350) {
            if(spellAge % 5 == 0) {
                playWarningSound();
            }
        }
    }

    @Override
    public int getDuration() {
        return ZauberConfig.getJuggernautSpellDuration();
    }

    @Override
    public void onEnd() {
        JuggernautSpell.clearJuggernautItems((ServerPlayerEntity) getCaster());
        if(getCaster().isAlive()) {
            getCaster().damage(getCaster().getDamageSources().magic(), 100f);
            getCaster().setHealth(0);
        }
    }

    public static void generateJuggernautItemAndSetToSlot(ServerPlayerEntity player, int slot, Item item, long tick) {
        player.getInventory().setStack(slot, generateJuggernautItem(item, tick));

    }

    public static ItemStack generateJuggernautItem(Item item, long tickWorldtime) {
        return generateJuggernautItem(new ItemStack(item), tickWorldtime);
    }

    public static ItemStack generateJuggernautItem(ItemStack itemStack, long tickWorldtime) {
        if(itemStack.isEnchantable()) {
            enchantMax(itemStack);
        }
        ItemStackJuggernautModeAccessor.access(itemStack).zauber$setJuggernautModeTick(tickWorldtime);
        return itemStack;
    }

    public static void enchantMax(ItemStack itemStack) {
        enchantMax(itemStack, List.of(Enchantments.BANE_OF_ARTHROPODS, Enchantments.SMITE, Enchantments.KNOCKBACK, Enchantments.MENDING, Enchantments.FROST_WALKER, Enchantments.FIRE_PROTECTION, Enchantments.PROJECTILE_PROTECTION, Enchantments.BLAST_PROTECTION));
    }

    public static void enchantMax(ItemStack itemStack, List<Enchantment> excludedEnchantments) {
        var enchantments = new ArrayList<>(Registries.ENCHANTMENT.stream().toList());
        enchantments.removeAll(excludedEnchantments);
        for(Enchantment enchantment : enchantments) {
            if(enchantment.isAcceptableItem(itemStack)) {
                itemStack.addEnchantment(enchantment, enchantment.getMaxLevel() + 1);
            }
        }
    }


    public static void clearJuggernautItems(ServerPlayerEntity player) {
        List<DefaultedList<ItemStack>> combinedInventory = ImmutableList.of(player.getInventory().main, player.getInventory().armor, player.getInventory().offHand);
        for (List<ItemStack> list : combinedInventory) {
            for (int i = 0; i < list.size(); ++i) {
                ItemStack itemStack = list.get(i);

                if (itemStack.isEmpty()) continue;
                if (ItemStackJuggernautModeAccessor.access(itemStack).zauber$getJuggernautTick() <= 0L)continue;
                list.set(i, ItemStack.EMPTY);
            }
        }
    }
}
