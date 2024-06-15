package dev.louis.zauber.item;

import dev.louis.zauber.Zauber;
import dev.louis.zauber.data.DarkTotemAttachment;
import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TotemOfDarknessItem extends Item implements PolymerItem {
    public TotemOfDarknessItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);


        var totemData = user.getAttachedOrCreate(Zauber.DARK_TOTEM_ATTACHMENT_TYPE);
        if (!totemData.present()) {
            if (!user.getAbilities().creativeMode) {
                itemStack.decrement(1);
            }

            user.setAttached(Zauber.DARK_TOTEM_ATTACHMENT_TYPE, new DarkTotemAttachment(true));

            return TypedActionResult.success(itemStack, world.isClient());
        }
        return TypedActionResult.pass(itemStack);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return Zauber.isClientModded(player) ? itemStack.getItem() : Items.TOTEM_OF_UNDYING;
    }
}
