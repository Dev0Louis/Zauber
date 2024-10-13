package dev.louis.zauber.item;

import dev.louis.zauber.duck.PlayerEntityExtension;
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

    public StaffItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        var stack = user.getStackInHand(hand);
        if (!world.isClient() && stack.isOf(ZauberItems.STAFF)) {
            var ext = (PlayerEntityExtension) user;

            if (!user.isSneaking()) {
                var optional = ext.getStaffTargetedBlock();
                if (optional.isPresent()) {
                    var cBlockPos = optional.get();
                    var blockPos = cBlockPos.getBlockPos();
                    var realState = cBlockPos.getBlockState();
                    var state = realState.contains(Properties.WATERLOGGED) ? realState.with(Properties.WATERLOGGED, Boolean.FALSE) : realState;

                    world.setBlockState(blockPos, realState.getFluidState().getBlockState(), Block.NOTIFY_ALL);

                    BlockTelekinesisEntity blockTelekinesisEntity = new BlockTelekinesisEntity(world, blockPos.toCenterPos(), state, cBlockPos.getBlockEntity(), user);
                    ((PlayerEntityExtension) user).zauber$startTelekinesisOn(blockTelekinesisEntity);
                    world.spawnEntity(blockTelekinesisEntity);
                    return TypedActionResult.success(stack);
                } else {
                    var optional1 = ext.getStaffTargetedEntity();
                    if (optional1.isPresent()) {
                        var entity = optional1.get();
                        ((PlayerEntityExtension) user).zauber$startTelekinesisOn(entity);

                    }
                }
            }

            ext.zauber$stopTelekinesis();
        }

        return TypedActionResult.pass(stack);
    }

    //called on server only
    public void throwBlock(World world, PlayerEntity player, ItemStack stack) {
        ((PlayerEntityExtension) player).zauber$throwTelekinesis();
    }
}
