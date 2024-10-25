package dev.louis.zauber.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class StaffItem extends Item {
    public static ClientAction CLIENT_ACTION = ((world, user, hand) -> TypedActionResult.pass(user.getStackInHand(hand)));

    public StaffItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient()) {
            return CLIENT_ACTION.use(world, user, hand);
        }

        return TypedActionResult.success(user.getStackInHand(hand));
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (user.getWorld().isClient()) {
            CLIENT_ACTION.use(user.getWorld(), user, hand);
        }

        return ActionResult.CONSUME;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getWorld().isClient()) {
            CLIENT_ACTION.use(context.getWorld(), context.getPlayer(), context.getHand());
        }

        return ActionResult.CONSUME;
    }

    public interface ClientAction {
        TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand);
    }
}
