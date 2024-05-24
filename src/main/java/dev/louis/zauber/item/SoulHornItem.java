package dev.louis.zauber.item;

import dev.louis.zauber.Zauber;
import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.utils.PolymerClientDecoded;
import eu.pb4.polymer.core.api.utils.PolymerKeepModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SoulHornItem extends Item implements PolymerItem, PolymerKeepModel, PolymerClientDecoded {

    public SoulHornItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(user instanceof ServerPlayerEntity serverUser && !user.getItemCooldownManager().isCoolingDown(this)) {
            if(!Zauber.isClientModded(serverUser)) return TypedActionResult.fail(user.getStackInHand(hand));
            boolean casted = serverUser.getSpellManager().cast(Zauber.Spells.MANA_HORSE);
            if (casted) {
                return TypedActionResult.consume(user.getStackInHand(hand));
            }
        }
        return TypedActionResult.fail(user.getStackInHand(hand));
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        if(Zauber.isClientModded(player)) return this;
        return Items.GOAT_HORN;
    }
}
