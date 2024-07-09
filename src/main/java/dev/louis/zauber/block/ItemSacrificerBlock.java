package dev.louis.zauber.block;

import com.mojang.serialization.MapCodec;
import dev.louis.zauber.block.entity.ItemSacrificerBlockEntity;
import dev.louis.zauber.helper.ShutUpAboutBlockStateModels;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@ShutUpAboutBlockStateModels
public class ItemSacrificerBlock extends BlockWithEntity implements PolymerBlock {
    public static final MapCodec<ItemSacrificerBlock> CODEC = createCodec(ItemSacrificerBlock::new);

    public ItemSacrificerBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ItemSacrificerBlockEntity itemSacrificerBlockEntity) {
            return itemSacrificerBlockEntity.offerItemStack(player, ItemStack.EMPTY);
        }

        return ActionResult.SUCCESS;
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (hand == Hand.OFF_HAND || world.isClient()) return ItemActionResult.FAIL;

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ItemSacrificerBlockEntity itemSacrificerBlockEntity) {
            if (!itemSacrificerBlockEntity.offerItemStack(player, stack).isAccepted()) {
                return ItemActionResult.FAIL;
            }
        }

        return ItemActionResult.SUCCESS;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        float l = 8.0F - 4;
        float m = 8.0F + 4;
        VoxelShape voxelShape = Block.createCuboidShape(l, 0.0, l, m, 24.0F, m);
        return voxelShape;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        ItemScatterer.onStateReplaced(state, newState, world, pos);
        super.onStateReplaced(state, world, pos, newState, moved);
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
    public BlockState getPolymerBlockState(BlockState state) {
        return Blocks.STONE_BRICK_WALL.getDefaultState();
    }
}
