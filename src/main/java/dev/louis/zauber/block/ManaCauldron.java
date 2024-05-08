package dev.louis.zauber.block;

import com.mojang.serialization.MapCodec;
import dev.louis.zauber.helper.SoundHelper;
import dev.louis.zauber.tag.ZauberPotionTags;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.virtualentity.api.BlockWithMovingElementHolder;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.BlockBoundAttachment;
import eu.pb4.polymer.virtualentity.api.elements.BlockDisplayElement;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.PotionUtil;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collection;

public class ManaCauldron extends Block implements PolymerBlock, BlockWithMovingElementHolder {
    public static final MapCodec<ManaCauldron> CODEC = createCodec(ManaCauldron::new);
    public static final IntProperty MANA_LEVEL = IntProperty.of("mana_level", 0, 2);

    protected ManaCauldron(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(MANA_LEVEL, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(MANA_LEVEL);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        var manaLevel = state.get(MANA_LEVEL);
        if (!world.isClient && player.getStackInHand(hand).isOf(Items.POTION) && PotionUtil.getPotion(player.getStackInHand(hand)).getRegistryEntry().isIn(ZauberPotionTags.MANA) && manaLevel < 2) {
            int newManaLevel = Math.max(Math.min(manaLevel + 1, 2), 0);
            world.setBlockState(pos, state.with(MANA_LEVEL, newManaLevel));
            player.getStackInHand(hand).decrement(1);
            player.getInventory().offerOrDrop(Items.GLASS_BOTTLE.getDefaultStack());
            SoundHelper.playBlockSound((ServerWorld) world, pos, SoundEvents.ITEM_BOTTLE_EMPTY);
            return ActionResult.CONSUME;
        }
        return ActionResult.PASS;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return CauldronBlock.OUTLINE_SHAPE;
    }

    @Override
    public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return CauldronBlock.RAYCAST_SHAPE;
    }

    @Override
    protected MapCodec<? extends Block> getCodec() {
        return CODEC;
    }

    @Override
    public Block getPolymerBlock(BlockState state) {
        return Blocks.CAULDRON;
    }

    @Override
    public boolean tickElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        return true;
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        return new CustomHolder(initialBlockState);
    }

    public static class CustomHolder extends ElementHolder {
        private static final BlockState EMPTY_STATE = Blocks.AIR.getDefaultState();
        private static final BlockState HALF_FILLED_STATE = Blocks.LIGHT_BLUE_STAINED_GLASS.getDefaultState();
        private static final BlockState FULL_STATE = Blocks.LIGHT_BLUE_STAINED_GLASS.getDefaultState();
        private static final BlockState MANA_BUBBLE_STATE = Blocks.LIGHT_BLUE_STAINED_GLASS.getDefaultState();
        private final BlockDisplayElement manaFill;
        private final Collection<BlockDisplayElementWithVelocity> manaBubbles = new ArrayList<>();
        private final Random random = Random.create();
        private int age;

        public CustomHolder(BlockState initialBlockState) {
            this.manaFill = this.addElement(new BlockDisplayElement(this.getState(initialBlockState)));
            this.manaFill.setOffset(new Vec3d(-0.375, 0, -0.375));
            this.manaFill.setScale(new Vector3f(0.75f, 0.2f * initialBlockState.get(MANA_LEVEL) + (float)Math.sin(age / 50f) * 0.05f, 0.75f));
            this.manaFill.setGlowing(false);
        }

        @Override
        public void onTick() {
            //connection.tick();
            //connection.setGlowing(true);
            this.age++;
            this.manaFill.setOffset(new Vec3d(-0.375, 0, -0.375));
            var attachment = this.getAttachment();
            if (attachment == null) throw new IllegalStateException("Attachment is null");
            var blockBoundAttachment = ((BlockBoundAttachment)attachment);
            var blockPos = blockBoundAttachment.getBlockPos();
            var manaLevel = blockBoundAttachment.getBlockState().get(MANA_LEVEL);
            var yOffset = (float)Math.sin(age / 50f) * 0.05f;
            this.manaFill.setScale(new Vector3f(0.75f, 0.2f * manaLevel + yOffset, 0.75f));
            this.manaFill.setBlockState(this.getState(this.getAttachment().getWorld(), blockPos));

            var world = attachment.getWorld();
            if (manaLevel > 0) {
                if (age % (4 - manaLevel) == 0) {
                    world.spawnParticles(ParticleTypes.UNDERWATER, blockPos.getX() + 0.5, blockPos.getY() + 0.75 + yOffset, blockPos.getZ() + 0.5, 10, 0.15, 0.15, 0.15, 1);
                }
                if (manaBubbles.size() < manaLevel * 10) {
                    Vec3d velocity = new Vec3d((random.nextFloat() - 0.5) * 0.2, 0.05f * manaLevel, (random.nextFloat() - 0.5) * 0.2);
                    var blockDisplayEntity = new BlockDisplayElementWithVelocity(MANA_BUBBLE_STATE, velocity);
                    blockDisplayEntity.setScale(new Vector3f(0.1f));
                    this.addElement(blockDisplayEntity);
                    manaBubbles.add(blockDisplayEntity);
                }
            }

            manaBubbles.forEach(BlockDisplayElementWithVelocity::tick);
            manaBubbles.removeIf(element -> {
                var remove = element.getOffset().getY() > 3 * manaLevel || random.nextFloat() > 0.93f;
                if (remove) {
                    var pos = this.getAttachment().getPos().add(element.getOffset());
                    if (random.nextFloat() > 0.5f) {
                        SoundHelper.playSound(
                                world,
                                pos,
                                SoundEvents.BLOCK_LAVA_POP,
                                SoundCategory.AMBIENT,
                                0.3f,
                                (random.nextFloat())
                        );
                    }
                    world.spawnParticles(
                            ParticleTypes.BUBBLE_POP,
                            pos.x,
                            pos.y,
                            pos.z,
                            1,
                            0,
                            0,
                            0,
                            0
                    );
                    this.removeElement(element);
                }
                return remove;
            });

        }

        public BlockState getState(ServerWorld world, BlockPos pos) {
            return this.getState(world.getBlockState(pos));
        }

        public BlockState getState(BlockState state) {
            var manaLevel = state.get(MANA_LEVEL);
            return switch (manaLevel) {
                case 0 -> EMPTY_STATE;
                case 1 -> HALF_FILLED_STATE;
                case 2 -> FULL_STATE;
                default -> throw new IllegalStateException("Unexpected value: " + manaLevel);
            };
        }
    }

    public static class BlockDisplayElementWithVelocity extends BlockDisplayElement {
        private Vec3d velocity;

        public BlockDisplayElementWithVelocity(BlockState state, Vec3d velocity) {
            super(state);
            this.velocity = velocity;
        }


        @Override
        public void tick() {
            super.tick();
            velocity = velocity.multiply(0.9, 1, 0.9);
            this.setOffset(this.getOffset().add(velocity));
        }
    }
}
