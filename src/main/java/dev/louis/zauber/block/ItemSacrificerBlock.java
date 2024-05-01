package dev.louis.zauber.block;

import com.mojang.serialization.MapCodec;
import dev.louis.zauber.block.entity.ItemSacrificerBlockEntity;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ItemSacrificerBlock extends BlockWithEntity implements PolymerBlock {
    public static final MapCodec<ItemSacrificerBlock> CODEC = createCodec(ItemSacrificerBlock::new);

    public ItemSacrificerBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (hand == Hand.OFF_HAND || world.isClient()) return ActionResult.FAIL;
        ItemStack itemStack = player.getStackInHand(hand);

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ItemSacrificerBlockEntity itemSacrificerBlockEntity) {
            return itemSacrificerBlockEntity.offerItemStack(player, itemStack);
        }

        return ActionResult.SUCCESS;
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ItemSacrificerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient() ? null : validateTicker(type, ItemSacrificerBlockEntity.TYPE, ItemSacrificerBlockEntity::tick);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public Block getPolymerBlock(BlockState state) {
        return Blocks.RESPAWN_ANCHOR;
    }
}
