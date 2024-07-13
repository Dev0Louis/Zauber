package dev.louis.zauber.block;

import com.mojang.serialization.MapCodec;
import dev.louis.zauber.block.entity.RitualStoneBlockEntity;
import dev.louis.zauber.helper.ShutUpAboutBlockStateModels;
import dev.louis.zauber.helper.SoundHelper;
import dev.louis.zauber.poi.ZauberPointOfInterestTypes;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.virtualentity.api.BlockWithMovingElementHolder;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.BlockBoundAttachment;
import eu.pb4.polymer.virtualentity.api.elements.BlockDisplayElement;
import net.minecraft.block.*;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

@ShutUpAboutBlockStateModels
public class ManaCauldron extends AbstractCauldronBlock implements PolymerBlock, BlockWithMovingElementHolder {
    public static final MapCodec<ManaCauldron> CODEC = createCodec(ManaCauldron::new);
    public static final IntProperty MANA_LEVEL = IntProperty.of("mana_level", 1, 2);

    public static final CauldronBehavior.CauldronBehaviorMap MANA_CAULDRON_BEHAVIOR = CauldronBehavior.createMap("mana");

    static {
        MANA_CAULDRON_BEHAVIOR.map().put(Items.POTION, ((state, world, pos, player, hand, stack) -> {
            if (!world.isClient && state.get(MANA_LEVEL) < 2) {
                Item item = stack.getItem();
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
                player.incrementStat(Stats.USE_CAULDRON);
                player.incrementStat(Stats.USED.getOrCreateStat(item));
                world.setBlockState(pos, state.with(MANA_LEVEL, state.get(MANA_LEVEL) + 1));
                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
            }
            return ItemActionResult.success(world.isClient);
        }));
    }

    protected ManaCauldron(Settings settings) {
        super(settings, MANA_CAULDRON_BEHAVIOR);
        setDefaultState(getDefaultState().with(MANA_LEVEL, 1));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(MANA_LEVEL);
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
    public boolean isFull(BlockState state) {
        return state.get(MANA_LEVEL) >= 2;
    }

    @Override
    protected MapCodec<? extends AbstractCauldronBlock> getCodec() {
        return CODEC;
    }

    @Override
    public boolean tickElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        return true;
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        return new CustomHolder(initialBlockState);
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state) {
        return Blocks.CAULDRON.getDefaultState();
    }

    public static class CustomHolder extends ElementHolder {
        private static final BlockState MANA_FILL_STATE = Blocks.LIGHT_BLUE_STAINED_GLASS.getDefaultState();
        private static final BlockState MANA_BUBBLE_STATE = Blocks.LIGHT_BLUE_STAINED_GLASS.getDefaultState();
        private final BlockDisplayElement manaFill;
        private final Collection<ManaBlockDisplayElement> manaBubbles = new ArrayList<>();
        private final Random random = Random.create();
        private int age;
        @Nullable
        private RitualStoneBlockEntity ritualStone;

        public CustomHolder(BlockState initialBlockState) {
            this.manaFill = this.addElement(new BlockDisplayElement(MANA_FILL_STATE));
            this.manaFill.setOffset(new Vec3d(-0.375, 0, -0.375));
            this.manaFill.setScale(new Vector3f(0.75f, 0.2f * initialBlockState.get(MANA_LEVEL) + (float)Math.sin(age / 50f) * 0.05f, 0.75f));
            this.manaFill.setBlockState(MANA_FILL_STATE);
            this.manaFill.setGlowing(false);
        }

        @Override
        public void onTick() {
            //connection.deletionTick();
            //connection.setGlowing(true);
            this.age++;
            var attachment = this.getAttachment();
            if (attachment == null) throw new IllegalStateException("Attachment is null");
            var blockBoundAttachment = ((BlockBoundAttachment)attachment);
            var blockPos = blockBoundAttachment.getBlockPos();
            int manaLevel = blockBoundAttachment.getBlockState().get(MANA_LEVEL);
            var yOffset = (float)Math.sin(age / 50f) * 0.05f;
            this.manaFill.setScale(new Vector3f(0.75f, 0.2f * manaLevel + yOffset, 0.75f));

            var world = attachment.getWorld();
            if (manaLevel > 0) {
                if (age % (4 - manaLevel) == 0) {
                    world.spawnParticles(ParticleTypes.UNDERWATER, blockPos.getX() + 0.5, blockPos.getY() + 0.75 + yOffset, blockPos.getZ() + 0.5, 10, 0.15, 0.15, 0.15, 1);
                }
                if (manaBubbles.size() < manaLevel * 10) {
                    Vec3d velocity = new Vec3d((random.nextFloat() - 0.5) * 0.2, 0.05f * manaLevel, (random.nextFloat() - 0.5) * 0.2);
                    var blockDisplayEntity = new ManaBlockDisplayElement(world, MANA_BUBBLE_STATE, velocity, age);
                    blockDisplayEntity.setInterpolationDuration(6);
                    blockDisplayEntity.setScale(new Vector3f(0.1f));
                    this.addElement(blockDisplayEntity);
                    manaBubbles.add(blockDisplayEntity);
                }
            }

            manaBubbles.forEach(ManaBlockDisplayElement::tick);

            Stream<PointOfInterest> stream = world.getPointOfInterestStorage()
                    .getInSquare(
                            poiType -> poiType.matchesKey(ZauberPointOfInterestTypes.RITUAL_BLOCK_KEY),
                            BlockPos.ofFloored(CustomHolder.this.getPos()),
                            20,
                            PointOfInterestStorage.OccupationStatus.ANY
                    );
            this.ritualStone = stream.map(poi -> world.getBlockEntity(poi.getPos(), RitualStoneBlockEntity.TYPE).orElse(null)).filter(Objects::nonNull).filter(RitualStoneBlockEntity::shouldManaCircle).findAny().orElse(null);

            manaBubbles.removeIf(element -> {
                boolean remove;
                if (this.ritualStone == null) {
                    remove = element.getOffset().getY() > 3 * manaLevel || random.nextFloat() > 0.93f;
                } else {
                    remove = element.getCurrentPos().isWithinRangeOf(ritualStone.getPos().toCenterPos(), 0.6, 0.6);
                }
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

        public class ManaBlockDisplayElement extends BlockDisplayElement {
            private final ServerWorld world;
            private Vec3d velocity;
            private final int creationAge;

            public ManaBlockDisplayElement(ServerWorld world, BlockState state, Vec3d velocity, int creationAge) {
                super(state);
                this.world = world;
                this.velocity = velocity;
                this.creationAge = creationAge;
            }

            @Override
            public void tick() {
                super.tick();
                if (CustomHolder.this.ritualStone != null) {
                    //A Ritual Stone that pulls mana was found.
                    var speed = ritualStone.getManaCirclingSpeed();
                    var distance = ritualStone.getManaCirclingDistance();
                    var progress = (this.creationAge + world.getTime());
                    var targetPos = ritualStone.getPos().toCenterPos().add(0, 0.5, 0);

                    velocity = velocity.multiply(0.9).add(this.getCurrentPos().relativize(targetPos).normalize().multiply(0.01));
                    //velocity.multiply(0.9);
                } else {
                    //No Ritual Stone that pulls Mana wasn't found
                    velocity = velocity.multiply(0.9, 1, 0.9);
                }
                this.setOffset(this.getOffset().add(velocity));
            }
        }

    }
}
