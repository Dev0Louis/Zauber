package dev.louis.zauber.item;

import dev.louis.zauber.LostBookType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class LostBookItem extends Item {
    public LostBookItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        var itemStack = user.getStackInHand(hand);
        if (world.isClient()) return TypedActionResult.pass(itemStack);
        var nbt = itemStack.getNbt();
        if (nbt != null) {
            LostBookType.getById(Identifier.tryParse(nbt.getString("lostBookId"))).ifPresent(user::openHandledScreen);
            return TypedActionResult.consume(itemStack);
        } else {
            if (user.isCreative()) {
                itemStack.getOrCreateNbt().putString("lostBookId", String.valueOf(LostBookType.getRandom(user.getRandom()).id()));
                /* recursion hehe */
                user.sendMessage(Text.literal("Generating random book...").formatted(Formatting.BLUE));
                return use(world, user, hand);
            }
            return TypedActionResult.fail(itemStack);
        }
    }
}
