package dev.louis.zauber.block;

import com.mojang.serialization.MapCodec;
import dev.louis.zauber.block.entity.ManaStorageBlockEntity;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ManaStorageBlock extends BlockWithEntity implements PolymerBlock {
    public static final MapCodec<ManaStorageBlock> CODEC = createCodec(ManaStorageBlock::new);
    public static final IntProperty MANA_LEVEL = IntProperty.of("mana_level", 0, 2);

    protected ManaStorageBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(MANA_LEVEL, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(MANA_LEVEL);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient && hand == Hand.MAIN_HAND) {
            int i = player.isSneaking() ? -1 : +1;
            int newManaLevel = Math.max(Math.min(state.get(MANA_LEVEL) + i, 2), 0);
            player.sendMessage(Text.of("Mana level: " + newManaLevel), true);
            world.setBlockState(pos, state.with(MANA_LEVEL, newManaLevel));
            return ActionResult.CONSUME;
        }
        return ActionResult.PASS;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ManaStorageBlockEntity(pos, state);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public Block getPolymerBlock(BlockState state) {
        return switch (state.get(MANA_LEVEL)) {
            case 0 -> Blocks.BLACK_STAINED_GLASS;
            case 1 -> Blocks.LIGHT_BLUE_STAINED_GLASS;
            case 2 -> Blocks.BLUE_STAINED_GLASS;
            default -> throw new IllegalStateException("Unexpected value: " + state.get(MANA_LEVEL));
        };
    }
}
