package dev.louis.zauber.item;

import dev.louis.zauber.LostBookType;
import dev.louis.zauber.component.item.ZauberDataComponentTypes;
import dev.louis.zauber.component.item.type.LostBookIdComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
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
        var lostBookContent = itemStack.get(ZauberDataComponentTypes.LOST_BOOK_CONTENT);
        if (lostBookContent != null) {
            LostBookType.getById(lostBookContent.id()).ifPresent(user::openHandledScreen);
            return TypedActionResult.consume(itemStack);
        } else {
            if (user.isCreative()) {
                itemStack.set(ZauberDataComponentTypes.LOST_BOOK_CONTENT, new LostBookIdComponent(LostBookType.getRandom(user.getRandom()).id()));
                user.sendMessage(Text.literal("Generating random book...").formatted(Formatting.BLUE));
                return use(world, user, hand);
            }
            return TypedActionResult.fail(itemStack);
        }
    }
}
