package dev.louis.zauber.block.entity;

import dev.louis.zauber.block.ManaCauldron;
import dev.louis.zauber.block.ZauberBlocks;
import dev.louis.zauber.particle.ZauberParticleTypes;
import dev.louis.zauber.poi.ZauberPointOfInterestTypes;
import dev.louis.zauber.ritual.Ritual;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.ChunkAttachment;
import eu.pb4.polymer.virtualentity.api.elements.BlockDisplayElement;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import eu.pb4.polymer.virtualentity.api.tracker.DisplayTrackedData;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.Brightness;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.ExplosionBehavior;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

public class RitualStoneBlockEntity extends BlockEntityWithItemStack {
    public static final BlockEntityType<RitualStoneBlockEntity> TYPE = BlockEntityType.Builder.create(RitualStoneBlockEntity::new, ZauberBlocks.RITUAL_STONE).build(null);
    private static final boolean EXPLOSION_CHAINS = true;
    private static final BlockState INACTIVE_STATE = Blocks.OBSIDIAN.getDefaultState();
    private static final BlockState ACTIVE_STATE = Blocks.LIGHT_BLUE_STAINED_GLASS.getDefaultState();
    private static final BlockState WARNING_STATE = Blocks.REDSTONE_BLOCK.getDefaultState();

    private final ElementHolder holder;
    @Nullable
    private Ritual ritual;
    private State state = State.INACTIVE;
    private boolean firstTick = true;
    private ItemDisplayElement itemDisplayElement;
    private Collection<BlockDisplayElement> blockDisplayElements = new ArrayList<>();
    private int interactionTimes;

    public RitualStoneBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE, pos, state);
        this.holder = new ElementHolder();
    }

    public void init() {
        ChunkAttachment.ofTicking(holder, (ServerWorld) world, pos.up());
        this.itemDisplayElement = new ItemDisplayElement(storedStack);
        itemDisplayElement.setScale(new Vector3f(0.5f));
        //element.setBillboardMode(DisplayEntity.BillboardMode.VERTICAL);
        this.holder.addElement(itemDisplayElement);

        this.blockDisplayElements.add(createBlockDisplayElement(-0.5, -0.5));
        this.blockDisplayElements.add(createBlockDisplayElement(-0.5, 0.4));

        this.blockDisplayElements.add(createBlockDisplayElement(0.4, -0.5));
        this.blockDisplayElements.add(createBlockDisplayElement(0.4, 0.4));
        this.blockDisplayElements.forEach(this.holder::addElement);

        this.firstTick = false;
    }

    public BlockDisplayElement createBlockDisplayElement(double x, double z) {
        var blockDisplayElement = new BlockDisplayElement(INACTIVE_STATE);
        blockDisplayElement.setScale(new Vector3f(0.1f));
        blockDisplayElement.setOffset(new Vec3d(x, -0.5, z));
        return blockDisplayElement;
    }

    public void tick(World world, BlockPos blockPos, BlockState state) {
        if (firstTick) this.init();
        itemDisplayElement.setOffset(new Vec3d(0, -0.25, 0));
        this.itemDisplayElement.setRightRotation(
                RotationAxis.POSITIVE_Y.rotationDegrees(world.getTime() / 5f)
        );

        this.spawnConnectionParticle();

        if (world.getTime() % 100 == 0) {
            this.interactionTimes = 0;
            this.blockDisplayElements.forEach(blockDisplayElement -> {
                blockDisplayElement.getDataTracker().set(DisplayTrackedData.BRIGHTNESS, -1);
            });
        }

        this.blockDisplayElements.forEach(blockDisplayElement -> {
            if (interactionTimes > 6) {
                blockDisplayElement.setBlockState(WARNING_STATE);
                blockDisplayElement.setBrightness(new Brightness(15, 15));
            } else if (this.state == State.ACTIVE) {
                blockDisplayElement.setBlockState(ACTIVE_STATE);
            } else if (this.state == State.INACTIVE) {
                blockDisplayElement.setBlockState(INACTIVE_STATE);
            }
        });

        switch (this.state) {
            //case READY -> tickReady(world);
            case ACTIVE -> tickActive(world);
        }
    }

    private void tickActive(World world) {
        if (ritual == null) {
            this.fail(true, false);
            return;
        }

        ritual.age++;
        ritual.tick();

        if(ritual.shouldStop()) {
            this.state = State.INACTIVE;
            ritual.finish();
            ritual = null;
        }
    }

    private void fail(boolean hardFail, boolean explosionChains) {
        if (hardFail) {
            world.createExplosion(
                    null,
                    world.getDamageSources().explosion(null),
                    new ExplosionBehavior(),
                    this.pos.toCenterPos(),
                    3,
                    false,
                    World.ExplosionSourceType.BLOCK
            );
            if (explosionChains) {
                Stream.concat(this.getFilledManaStorages(), this.getItemSacrificers().map(BlockEntity::getPos)).forEach(pos -> {
                    world.createExplosion(
                            null,
                            world.getDamageSources().explosion(null),
                            new ExplosionBehavior(),
                            pos.toCenterPos(),
                            3,
                            false,
                            World.ExplosionSourceType.BLOCK
                    );
                });
            }
        }
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
        System.out.println("CLICKED " + player.getNameForScoreboard() + " state " + this.state + " storedStack " + this.storedStack);

        switch (this.state) {
            case INACTIVE -> {

                var handStack = player.getMainHandStack().copyWithCount(1);
                if (!handStack.isEmpty()) {
                    player.getMainHandStack().decrement(1);
                    this.giveBackRitualItem(player);
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
                    this.fail(true, true);
                } else {
                    world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_LODESTONE_BREAK, SoundCategory.BLOCKS);
                    //world.playSound();
                }
                // No ritual could start. Sad :(
            }
            case ACTIVE -> {
                this.fail(true, false);
            }
        }
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
        if(world.getTime() % 15 == 0) {
            this.getItemSacrificers().forEach(blockEntity -> {
                Vec3d blockPos = blockEntity.getPos().toCenterPos();
                blockPos = blockPos.add(0, 1 + ItemSacrificerBlockEntity.getItemOffset(blockEntity), 0);

                spawnConnectionParticles(blockPos, ritualPos);
            });
            this.getFilledManaStorages().forEach(blockPos -> {
                spawnConnectionParticles(blockPos.toCenterPos(), ritualPos);
            });
        }
    }

    private void spawnConnectionParticles(Vec3d blockPos, Vec3d ritualPos) {
        final int steps = 10;
        for (int i = 0; i < steps; i++) {
            var delta = (double) i / steps;
            delta = (delta * 0.9) + 0.1;
            var x = MathHelper.lerp(delta, blockPos.x, ritualPos.x);
            var y = MathHelper.lerp(delta, blockPos.y, ritualPos.y);
            var z = MathHelper.lerp(delta, blockPos.z, ritualPos.z);
            Vec3d pos = new Vec3d(x, y, z);


            ((ServerWorld)world).spawnParticles(
                    ZauberParticleTypes.MANA_RUNE,
                    pos.getX(),
                    pos.getY(),
                    pos.getZ(),
                    1,
                    0,
                    0,
                    0,
                    0.0
            );
        }
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
        if (world instanceof ServerWorld serverWorld) {
            return getRitualBlockPoses().map(poi -> serverWorld.getBlockEntity(poi.getPos(), ItemSacrificerBlockEntity.TYPE)).filter(Optional::isPresent).map(Optional::get).filter(blockEntity -> blockEntity.storedStack != ItemStack.EMPTY);
        }
        return Stream.empty();
    }


    public Stream<BlockPos> getFilledManaStorages() {
        if (world instanceof ServerWorld serverWorld) {
            return getRitualBlockPoses().filter(poi -> {
                var blockState = world.getBlockState(poi.getPos());
                return blockState.isOf(ZauberBlocks.MANA_STORAGE) && blockState.get(ManaCauldron.MANA_LEVEL) > 0;
            }).map(pointOfInterest -> pointOfInterest.getPos());
        }
        return Stream.empty();
    }

    public Stream<ItemStack> getAvailableItemStacks() {
        return this.getItemSacrificers().map(itemSacrificerBlockEntity -> itemSacrificerBlockEntity.storedStack);
    }

    @Override
    public void setStoredStack(ItemStack storedStack) {
        super.setStoredStack(storedStack);
        if (itemDisplayElement != null) itemDisplayElement.setItem(storedStack);
    }

    @Override
    public void markRemoved() {
        this.holder.destroy();
        super.markRemoved();
    }

    public enum State {
        INACTIVE,
        ACTIVE
    }
}
