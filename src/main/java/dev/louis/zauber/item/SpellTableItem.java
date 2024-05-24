package dev.louis.zauber.item;

import dev.louis.zauber.Zauber;
import eu.pb4.polymer.core.api.item.PolymerBlockItem;
import eu.pb4.polymer.core.api.utils.PolymerClientDecoded;
import eu.pb4.polymer.core.api.utils.PolymerKeepModel;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public class SpellTableItem extends PolymerBlockItem implements PolymerKeepModel, PolymerClientDecoded {
    public SpellTableItem(Block block, Settings settings) {
        super(block, settings, Items.ENCHANTING_TABLE);
    }


    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        if(Zauber.isClientModded(player))return this;
        return super.getPolymerItem(itemStack, player);
    }

}
