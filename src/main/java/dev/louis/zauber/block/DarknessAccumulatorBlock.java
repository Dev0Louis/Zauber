package dev.louis.zauber.block;

import com.mojang.serialization.MapCodec;
import dev.louis.zauber.block.entity.DarknessAccumulatorBlockEntity;
import dev.louis.zauber.helper.ShutUpAboutBlockStateModels;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.virtualentity.api.BlockWithElementHolder;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.BlockBoundAttachment;
import eu.pb4.polymer.virtualentity.api.elements.BlockDisplayElement;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.decoration.Brightness;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

@ShutUpAboutBlockStateModels
public class DarknessAccumulatorBlock extends TransparentBlock implements PolymerBlock, BlockEntityProvider, BlockWithElementHolder {
    public static final MapCodec<DarknessAccumulatorBlock> CODEC = createCodec(DarknessAccumulatorBlock::new);
    public static final BooleanProperty HAS_DARKNESS = BooleanProperty.of("has_darkness");

    public DarknessAccumulatorBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(HAS_DARKNESS, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HAS_DARKNESS);
    }

    @Override
    protected MapCodec<? extends TransparentBlock> getCodec() {
        return CODEC;
    }

    @Override
    public boolean tickElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.get(HAS_DARKNESS) && world.getBaseLightLevel(pos, 0) == 0) {
            if (random.nextFloat() > 0.9) world.setBlockState(pos, state.with(HAS_DARKNESS, true));
        }
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        return new CustomHolder(initialBlockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient() ? null : validateTicker(type, DarknessAccumulatorBlockEntity.TYPE, (world1, pos1, state1, blockEntity) -> blockEntity.tick(world1, pos1, state1));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DarknessAccumulatorBlockEntity(pos, state);
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> validateTicker(
            BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker
    ) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state) {
        return Blocks.GLASS.getDefaultState();
    }

    private static class CustomHolder extends ElementHolder {
        public static BlockState BAR_STATE = Blocks.IRON_BARS.getDefaultState();
        public static BlockState DARKNESS = Blocks.BLACK_CONCRETE.getDefaultState();
        public static BlockState AIR = Blocks.AIR.getDefaultState();
        private final BlockDisplayElement darkness;
        private final BlockDisplayElement sculkFloor;

        @SuppressWarnings("UnreachableCode")
        public CustomHolder(BlockState blockState) {
            var bar1 = this.addElement(new BlockDisplayElement(BAR_STATE));
            var bar2 = this.addElement(new BlockDisplayElement(BAR_STATE));
            var bar3 = this.addElement(new BlockDisplayElement(BAR_STATE));
            var bar4 = this.addElement(new BlockDisplayElement(BAR_STATE));
            bar1.setOffset(new Vec3d(-0.9375, -0.5, -0.0625));
            bar2.setOffset(new Vec3d(-0.0625, -0.5, -0.9375));
            bar3.setOffset(new Vec3d(-0.0625, -0.5, -0.0625));
            bar4.setOffset(new Vec3d(-0.9375, -0.5, -0.9375));

            darkness = this.addElement(new BlockDisplayElement(this.getDarknessState(blockState)));
            darkness.setBrightness(new Brightness(Integer.MIN_VALUE, Integer.MIN_VALUE));

            this.sculkFloor = this.addElement(new BlockDisplayElement(Blocks.SCULK.getDefaultState()));
        }

        private BlockState getDarknessState(BlockState state) {
            return state.getOrEmpty(DarknessAccumulatorBlock.HAS_DARKNESS).orElse(false) ? DARKNESS : AIR;
        }

        @Override
        protected void onTick() {
            darkness.setBlockState(this.getDarknessState(((BlockBoundAttachment)this.getAttachment()).getBlockState()));
            var size = 0.97f;
            darkness.setScale(new Vector3f(size));
            darkness.setOffset(new Vec3d(-size / 2f , -0.48, -size / 2f));
            size = 0.9f;
            this.sculkFloor.setScale(new Vector3f(0.9f, 0.1f, 0.9f));
            this.sculkFloor.setOffset(new Vec3d(-size / 2, -0.49, -size / 2));
        }
    }
}
