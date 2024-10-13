package dev.louis.zauber.item;

import dev.louis.zauber.extension.PlayerEntityExtension;
import dev.louis.zauber.entity.BlockTelekinesisEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
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
            CLIENT_ACTION.use(world, user, hand);
        }

        return TypedActionResult.success(user.getStackInHand(hand));
    }

    //called on server only
    public void throwBlock(World world, PlayerEntity player, ItemStack stack) {
        ((PlayerEntityExtension) player).zauber$throwTelekinesis();
    }

    public interface ClientAction {
        TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand);
    }
}
