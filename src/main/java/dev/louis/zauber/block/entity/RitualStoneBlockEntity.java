package dev.louis.zauber.block.entity;

import dev.louis.zauber.block.DarknessAccumulatorBlock;
import dev.louis.zauber.block.ManaCauldron;
import dev.louis.zauber.block.ZauberBlocks;
import dev.louis.zauber.helper.EffectHelper;
import dev.louis.zauber.helper.ParticleHelper;
import dev.louis.zauber.poi.ZauberPointOfInterestTypes;
import dev.louis.zauber.ritual.Ritual;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.ExplosionBehavior;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RitualStoneBlockEntity extends BlockEntityWithItemStack {
    private static final String[] RAI_NAMES = {"rai", "enjarai", "silliestpersonalive"};

    public static final BlockEntityType<RitualStoneBlockEntity> TYPE = BlockEntityType.Builder.create(RitualStoneBlockEntity::new, ZauberBlocks.RITUAL_STONE).build(null);
    private static final Vector3f PARTICLE_COLOR = new Vector3f(0, 0, 1);
    private static final boolean EXPLOSION_CHAINS = true;
    private static final BlockState INACTIVE_STATE = Blocks.OBSIDIAN.getDefaultState();
    private static final BlockState ACTIVE_STATE = Blocks.LIGHT_BLUE_STAINED_GLASS.getDefaultState();
    private static final BlockState WARNING_STATE = Blocks.REDSTONE_BLOCK.getDefaultState();

    @Nullable
    private Ritual ritual;
    private State state = State.INACTIVE;
    private int interactionTimes;

    public RitualStoneBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE, pos, state);
    }

    public void tick(World world, BlockPos blockPos, BlockState state) {
        this.spawnConnectionParticle();

        if (world.getTime() % 100 == 0) {
            this.interactionTimes = 0;
        }

        switch (this.state) {
            //case READY -> tickReady(world);
            case ACTIVE -> tickActive(world);
        }
    }

    private void tickActive(World world) {
        if (ritual == null) {
            this.fail();
            return;
        }
        if(ritual.shouldStop()) {
            this.state = State.INACTIVE;
            ritual.finish();
            ritual = null;
            return;
        }

        ritual.age++;
        ritual.tick();
    }

    private void fail() {
        world.createExplosion(
                null,
                world.getDamageSources().explosion(null),
                new ExplosionBehavior(),
                this.pos.toCenterPos(),
                3,
                false,
                World.ExplosionSourceType.BLOCK
        );

        world.playSound(
                null,
                pos,
                SoundEvents.BLOCK_PISTON_EXTEND,
                SoundCategory.BLOCKS,
                3,
                -40
        );
        if (storedStack != ItemStack.EMPTY) {
            dropRitualItem();
        }
        this.state = State.INACTIVE;
    }

    private void dropRitualItem() {
        if (storedStack != ItemStack.EMPTY) {
            var itemEntity = new ItemEntity(world, pos.getX(), pos.getY() + 1, pos.getZ(), storedStack.copy(), 0, 0.2, 0);
            world.spawnEntity(itemEntity);
            setStoredStack(ItemStack.EMPTY);
        }
    }

    private void giveBackRitualItem(PlayerEntity player) {
        player.getInventory().offerOrDrop(this.getStoredStack());
        this.setStoredStack(ItemStack.EMPTY);
    }

    public void onBlockClicked(PlayerEntity player, World world, BlockPos pos) {
        switch (this.state) {
            case INACTIVE -> {

                var handStack = player.getMainHandStack().copyWithCount(1);
                if (!handStack.isEmpty()) {
                    player.getMainHandStack().decrement(1);
                    this.giveBackRitualItem(player);
                    this.tryEasterEgg(player, handStack);
                    this.setStoredStack(handStack);
                    return;
                } else if (player.isSneaking()) {
                    this.giveBackRitualItem(player);
                    return;
                }


                this.ritual = createRitual(world, pos);
                if (ritual != null) {
                    ritual.onStart();
                    this.state = State.ACTIVE;
                    return;
                }
                interactionTimes++;
                if (interactionTimes > 10) {
                    this.getRandomNonEmptyItemSacrificer().ifPresentOrElse(itemSacrificerBlockEntity -> {
                        EffectHelper.playBreakItemEffect((ServerWorld) world, itemSacrificerBlockEntity.getPos().toCenterPos().add(0, 1, 0), itemSacrificerBlockEntity.getStoredStack());
                        itemSacrificerBlockEntity.setStoredStack(ItemStack.EMPTY);
                    }, () -> {
                        //if not present
                        if (!this.storedStack.isEmpty()) {
                            EffectHelper.playBreakItemEffect((ServerWorld) world, this.getPos().toCenterPos().add(0, 1, 0), this.getStoredStack());
                            this.setStoredStack(ItemStack.EMPTY);
                        }
                    });
                    //this.fail(true, true);
                } else {
                    world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_LODESTONE_BREAK, SoundCategory.BLOCKS);
                    //world.playSound();
                }
                // No ritual could start. Sad :(
            }
            case ACTIVE -> {
                this.fail();
            }
        }
    }



    private void tryEasterEgg(PlayerEntity player, ItemStack itemStack) {
        if (itemStack.isOf(Items.NETHER_WART) && ArrayUtils.contains(RAI_NAMES, itemStack.getName().getString())) {
            player.sendMessage(Text.literal("Zauber is not a rites ripoff...").setStyle(Style.EMPTY.withColor(Formatting.GRAY)), false);
        }
    }

    public Optional<ItemSacrificerBlockEntity> getRandomNonEmptyItemSacrificer() {
        return this.getNonEmptyItemSacrificers().collect(Collectors.collectingAndThen(Collectors.toList(), collected -> {
            Collections.shuffle(collected);
            return collected.stream();
        })).findAny();
    }


    public Optional<ItemSacrificerBlockEntity> getRandomItemSacrificer() {
        return this.getItemSacrificers().collect(Collectors.collectingAndThen(Collectors.toList(), collected -> {
            Collections.shuffle(collected);
            return collected.stream();
        })).findAny();
    }

    @Nullable
    private Ritual createRitual(World world, BlockPos pos) {
        for (Ritual.Starter starter : Ritual.RITUAL_STARTERS) {
            var ritual = starter.tryStart(world, this);
            if (ritual != null) return ritual;
        }
        return null;
    }

    protected void spawnConnectionParticle() {
        final Vec3d ritualPos = pos.toCenterPos();
        if(world.getTime() % 15 == 0 && ritual != null) {

            ritual.getConnections().forEach(position -> {
                spawnConnectionParticles(position, ritualPos);
            });
            /*this.getItemSacrificers().forEach(blockEntity -> {
                Vec3d blockPos = blockEntity.getPos().toCenterPos();
                blockPos = blockPos.add(0, 1 + ItemSacrificerBlockEntity.getItemOffset(blockEntity), 0);

                spawnConnectionParticles(blockPos, ritualPos);
            });
            this.getFilledManaStorages().forEach(blockPos -> {
                spawnConnectionParticles(blockPos.toCenterPos(), ritualPos);
            });*/
        }
    }

    private void spawnConnectionParticles(Position endPos, Position ritualPos) {
        ParticleHelper.spawnParticleLine(
                (ServerWorld) world,
                ritualPos,
                endPos,
                new DustParticleEffect(PARTICLE_COLOR, 1),
                10
        );
    }

    public Stream<PointOfInterest> getRitualBlockPoses() {
        if (world instanceof ServerWorld serverWorld) {
            return serverWorld.getPointOfInterestStorage()
                    .getInSquare(
                            poiType -> poiType.matchesKey(ZauberPointOfInterestTypes.RITUAL_BLOCKS_KEY),
                            this.pos,
                            20,
                            PointOfInterestStorage.OccupationStatus.ANY
                    );
        }
        return Stream.empty();
    }

    public Stream<ItemSacrificerBlockEntity> getItemSacrificers() {
        return getRitualBlockPoses().map(poi -> world.getBlockEntity(poi.getPos(), ItemSacrificerBlockEntity.TYPE)).filter(Optional::isPresent).map(Optional::get);
    }

    public Stream<ItemSacrificerBlockEntity> getNonEmptyItemSacrificers() {
        return this.getItemSacrificers().filter(blockEntity -> !blockEntity.storedStack.isEmpty());
    }

    public Stream<BlockPos> getFilledManaStorages() {
        return getRitualBlockPoses().filter(poi -> {
            var blockState = world.getBlockState(poi.getPos());
            return blockState.isOf(ZauberBlocks.MANA_CAULDRON) && blockState.get(ManaCauldron.MANA_LEVEL) > 0;
        }).map(PointOfInterest::getPos);
    }

    public Stream<BlockPos> getFullManaStorages() {
        return getRitualBlockPoses().filter(poi -> {
            BlockState blockState = world.getBlockState(poi.getPos());
            return blockState.isOf(ZauberBlocks.MANA_CAULDRON) && blockState.get(ManaCauldron.MANA_LEVEL) == 2;
        }).map(PointOfInterest::getPos);
    }

    public Stream<BlockPos> getFilledDarknessAccumulators() {
        if (world instanceof ServerWorld serverWorld) {
            var a = serverWorld.getPointOfInterestStorage()
                    .getInSquare(
                            poiType -> poiType.matchesKey(ZauberPointOfInterestTypes.DARKNESS_ACCUMULATOR_KEY),
                            this.pos,
                            20,
                            PointOfInterestStorage.OccupationStatus.ANY
                    ).toList();
            var b = a.stream().filter(poi -> world.getBlockState(poi.getPos()).get(DarknessAccumulatorBlock.HAS_DARKNESS)).map(PointOfInterest::getPos).toList();
            return b.stream();
        }
        return Stream.empty();
    }

    public Stream<ItemStack> getAvailableItemStacks() {
        return this.getNonEmptyItemSacrificers().map(itemSacrificerBlockEntity -> itemSacrificerBlockEntity.storedStack);
    }

    public State getState() {
        return state;
    }

    public int getInteractionTimes() {
        return interactionTimes;
    }

    @Override
    public void setStoredStack(ItemStack storedStack) {
        super.setStoredStack(storedStack);
    }

    public enum State {
        INACTIVE,
        ACTIVE
    }
}
