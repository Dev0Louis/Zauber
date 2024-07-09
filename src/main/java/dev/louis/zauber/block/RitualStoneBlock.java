package dev.louis.zauber.block;

import com.mojang.serialization.MapCodec;
import dev.louis.zauber.block.entity.BlockEntityWithItemStack;
import dev.louis.zauber.block.entity.RitualStoneBlockEntity;
import dev.louis.zauber.block.entity.RitualStoneBlockEntity.State;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.virtualentity.api.BlockWithElementHolder;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.BlockAwareAttachment;
import eu.pb4.polymer.virtualentity.api.elements.BlockDisplayElement;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.decoration.Brightness;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

public class RitualStoneBlock extends BlockWithEntity implements BlockWithElementHolder, PolymerBlock {
    public static final MapCodec<RitualStoneBlock> CODEC = createCodec(RitualStoneBlock::new);

    public RitualStoneBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RitualStoneBlockEntity(pos, state);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof RitualStoneBlockEntity ritualStoneBlockEntity) {
                ritualStoneBlockEntity.onInteracted(player, stack, world, pos);
                return ItemActionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
            }
        }
        return ItemActionResult.FAIL;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof RitualStoneBlockEntity ritualStoneBlockEntity) {
                ritualStoneBlockEntity.onInteracted(player, ItemStack.EMPTY, world, pos);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.FAIL;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient() ? null : validateTicker(type, RitualStoneBlockEntity.TYPE, ((world1, pos1, state1, blockEntity1) -> blockEntity1.tick(world1, pos1, state1)));
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        ItemScatterer.onStateReplaced(state, newState, world, pos);
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public boolean tickElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        return true;
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        return new CustomHolder(world, pos, initialBlockState);
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state) {
        return Blocks.LODESTONE.getDefaultState();
    }

    @SuppressWarnings("UnreachableCode")
    private static class CustomHolder extends ElementHolder {
        private static final BlockState INACTIVE_STATE = Blocks.OBSIDIAN.getDefaultState();
        private static final BlockState ACTIVE_STATE = Blocks.LIGHT_BLUE_STAINED_GLASS.getDefaultState();
        private static final BlockState WARNING_STATE = Blocks.REDSTONE_BLOCK.getDefaultState();
        private static final BlockState PLATE_STATE = Blocks.SMOOTH_STONE.getDefaultState();

        private final Collection<BlockDisplayElement> indicators = new ArrayList<>();
        private final BlockDisplayElement ritualPlate;
        private ItemDisplayElement itemDisplay;
        private int age;



        public CustomHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
            this.ritualPlate = new BlockDisplayElement(this.getState(world, initialBlockState));
            final float xSize = 0.8f;
            final float ySize = 1f;
            ritualPlate.setBlockState(PLATE_STATE);
            ritualPlate.setScale(new Vector3f(xSize, ySize, xSize));
            ritualPlate.setOffset(new Vec3d(-xSize / 2f, -.4, -xSize / 2f));
            ritualPlate.setBrightness(new Brightness(0, 15));
            this.addElement(ritualPlate);

            this.indicators.add(createBlockDisplayElement(-0.5, -0.5));
            this.indicators.add(createBlockDisplayElement(-0.5, 0.4));

            this.indicators.add(createBlockDisplayElement(0.4, -0.5));
            this.indicators.add(createBlockDisplayElement(0.4, 0.4));
            this.indicators.forEach(this::addElement);

            this.itemDisplay = new ItemDisplayElement();
            itemDisplay.setScale(new Vector3f(0.25f));
            itemDisplay.setOffset(new Vec3d(0, 1, 0));

            this.addElement(itemDisplay);


        }

        public BlockDisplayElement createBlockDisplayElement(double x, double z) {
            var blockDisplayElement = new BlockDisplayElement(INACTIVE_STATE);
            blockDisplayElement.setScale(new Vector3f(0.1f));
            blockDisplayElement.setOffset(new Vec3d(x, .5, z));
            return blockDisplayElement;
        }

        @Override
        protected void onTick() {
            this.age++;
            itemDisplay.setScale(new Vector3f(0.375f));
            itemDisplay.setOffset(new Vec3d(0, 0.8, 0));

            this.itemDisplay.setRightRotation(RotationAxis.POSITIVE_Y.rotationDegrees(this.age / 5f));
            BlockAwareAttachment attachment = (BlockAwareAttachment) this.getAttachment();
            if (attachment == null) throw new IllegalStateException("Attachment is null");
            this.ritualPlate.setBrightness(
                    new Brightness(
                            attachment.getWorld().getLightLevel(LightType.BLOCK, attachment.getBlockPos().up()),
                            attachment.getWorld().getLightLevel(LightType.SKY, attachment.getBlockPos().up())
                    )
            );
            this.itemDisplay.setItem(this.getStack(attachment.getWorld(), attachment.getBlockPos()));
            var optionalRitualStoneBlock = attachment.getWorld().getBlockEntity(attachment.getBlockPos(), RitualStoneBlockEntity.TYPE);
            optionalRitualStoneBlock.ifPresent(ritualStoneBlockEntity -> {
                BlockState blockState;
                State state = ritualStoneBlockEntity.getState();
                if (ritualStoneBlockEntity.getInteractionTimes() > 6) {
                    blockState = WARNING_STATE;
                } else if (state == State.ACTIVE) {
                    blockState = ACTIVE_STATE;
                } else {
                    blockState = INACTIVE_STATE;
                }

                indicators.forEach(blockDisplayElement -> {
                    blockDisplayElement.setBlockState(blockState);
                });
            });

        }

        public BlockState getState(ServerWorld world, BlockState state) {
            return INACTIVE_STATE;
        }

        public ItemStack getStack(ServerWorld world, BlockPos pos) {
            return world.getBlockEntity(pos, RitualStoneBlockEntity.TYPE).map(BlockEntityWithItemStack::getStoredStack).orElse(ItemStack.EMPTY);
        }
    }

    @SuppressWarnings("UnreachableCode")
    public static class ConnectionElement extends BlockDisplayElement {
        private static final BlockState DEFAULT_STATE = Blocks.BEDROCK.getDefaultState();
        private final Consumer<ConnectionElement> consumer;
        private Vec3d endOffset;
        private int age;

        public ConnectionElement(Vec3d startPos, Vec3d endPos, Consumer<ConnectionElement> consumer) {
            this(startPos.relativize(endPos), consumer);
        }

        public ConnectionElement(Vec3d endOffset, Consumer<ConnectionElement> consumer) {
            super(DEFAULT_STATE);
            this.endOffset = endOffset;
            this.consumer = consumer;
        }

        public void setEndOffset(Vec3d endOffset) {
            this.endOffset = endOffset;
        }

        public void setEndOffset(Vec3d startPos, Vec3d endPos) {
            this.endOffset = startPos.relativize(endPos);
        }

        @Override
        public void tick() {
            this.age++;
            this.setScale(new Vector3f(0.1f));
            this.setOffset(Vec3d.ZERO.lerp(endOffset, age / 200f));
            if (age > 200) {
                this.age = 0;
                consumer.accept(this);
            }
            super.tick();
        }
    }
}
