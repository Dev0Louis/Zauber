package dev.louis.zauber.block;

import com.mojang.serialization.MapCodec;
import dev.louis.zauber.block.entity.RitualItemSacrificerBlockEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RitualItemSacrificerBlock extends BlockWithEntity {
    public static final MapCodec<RitualItemSacrificerBlock> CODEC = createCodec(RitualItemSacrificerBlock::new);

    public RitualItemSacrificerBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RitualItemSacrificerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient() ? null : validateTicker(type, RitualItemSacrificerBlockEntity.TYPE, RitualItemSacrificerBlockEntity::tick);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof RitualItemSacrificerBlockEntity ritualItemSacrificerBlockEntity) {
                ritualItemSacrificerBlockEntity.informRitualBlocks(ritualStoneBlockEntity -> ritualStoneBlockEntity.onRitualBlockPlaced(pos));
            }
        }
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof RitualItemSacrificerBlockEntity ritualItemSacrificerBlockEntity) {
                ritualItemSacrificerBlockEntity.informRitualBlocks(ritualStoneBlockEntity -> ritualStoneBlockEntity.onRitualBlockRemoved(pos));
            }
        }
        return super.onBreak(world, pos, state, player);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
