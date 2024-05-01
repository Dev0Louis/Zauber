package dev.louis.zauber.block.entity;

import dev.louis.zauber.block.ManaStorageBlock;
import dev.louis.zauber.block.ZauberBlocks;
import dev.louis.zauber.particle.ZauberParticleTypes;
import dev.louis.zauber.poi.ZauberPointOfInterestTypes;
import dev.louis.zauber.ritual.Ritual;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.ChunkAttachment;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.ExplosionBehavior;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.stream.Stream;

public class RitualStoneBlockEntity extends BlockEntity {
    public static final BlockEntityType<RitualStoneBlockEntity> TYPE = BlockEntityType.Builder.create(RitualStoneBlockEntity::new, ZauberBlocks.RITUAL_STONE).build(null);
    private final ElementHolder holder;
    @Nullable
    private Ritual ritual;

    private ItemStack ritualItem = ItemStack.EMPTY;
    private State state = State.INACTIVE;
    private boolean firstTick = true;

    public RitualStoneBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE, pos, state);
        this.holder = new ElementHolder();
    }


    public void init() {
        ChunkAttachment.ofTicking(holder, (ServerWorld) world, pos.up());
        var element = new ItemDisplayElement(ritualItem);
        element.setScale(new Vector3f(0.5f));
        element.setBillboardMode(DisplayEntity.BillboardMode.CENTER);
        this.holder.addElement(element);


        this.firstTick = false;
    }

    public void tick(World world, BlockPos blockPos, BlockState state) {
        if (firstTick) this.init();
        this.holder.getElements().forEach(virtualElement -> {
            ((ItemDisplayElement) virtualElement).setItem(ritualItem);
            virtualElement.setOffset(new Vec3d(0, 0.2 * (Math.sin(world.getTime() / 30f) + 1), 0));
        });
        this.spawnConnectionParticle();
        switch (this.state) {
            case READY -> tickReady(world);
            case ACTIVE -> tickActive(world);
        }
    }

    private void tickActive(World world) {
        if (ritual == null) {
            fail(true);
            return;
        }

        ritual.age++;
        ritual.tick();

        if(ritual.shouldStop()) {
            this.state = State.INACTIVE;
            ritual.finish();
            ritualItem = ItemStack.EMPTY;
            ritual = null;
        }
    }

    private void tickReady(World world) {
        spawnConnectionParticle();
    }

    private void fail(boolean hardFail) {
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
        }
        world.playSound(
                null,
                pos,
                SoundEvents.BLOCK_PISTON_EXTEND,
                SoundCategory.BLOCKS,
                3,
                -40
        );
        if (ritualItem != ItemStack.EMPTY) {
            world.spawnEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), ritualItem.copy()));
            ritualItem = ItemStack.EMPTY;
        }
        this.state = State.INACTIVE;
    }

    public void onBlockClicked(PlayerEntity player, World world, BlockPos pos) {
        System.out.println("CLICKED " + player.getNameForScoreboard() + " state " + this.state);

        if (player.isSneaking()) {
            this.fail(false);
            return;
        }

        switch (this.state) {
            case READY -> {
                this.state = State.ACTIVE;
            }
            case INACTIVE -> {
                if (this.ritualItem == ItemStack.EMPTY) {
                    this.ritualItem = player.getMainHandStack().copyWithCount(1);
                    player.getMainHandStack().decrement(1);
                    var ritual = getRitual(world, pos);
                    if (ritual != null) {
                        ritual.onStart();
                        this.state = State.READY;
                        return;
                    }
                    // No ritual could start. Sad :(
                }
            }
        }
    }

    @Nullable
    private Ritual getRitual(World world, BlockPos pos) {
        for (Ritual.Starter starter : Ritual.RITUAL_STARTERS) {
            var ritual = starter.tryStart(world, this);
            if (ritual != null) return ritual;
        }
        return null;
    }



    protected void spawnConnectionParticle() {
        final Vec3d ritualPos = pos.toCenterPos();
        if(world.getTime() % 15 == 0) {
            Stream.concat(this.getManaStorages(), this.getItemSacrificers()).forEach(blockEntity -> {
                final int steps = 10;
                final Vec3d ritualBlockPos = blockEntity.getPos().toCenterPos();
                for (int i = 0; i < steps; i++) {
                    var delta = (double) i / steps;
                    delta = (delta * 0.9) + 0.1;
                    var x = MathHelper.lerp(delta, ritualBlockPos.x, ritualPos.x);
                    var y = MathHelper.lerp(delta, ritualBlockPos.y, ritualPos.y);
                    var z = MathHelper.lerp(delta, ritualBlockPos.z, ritualPos.z);
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
            });
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


    public Stream<ManaStorageBlockEntity> getManaStorages() {
        if (world instanceof ServerWorld serverWorld) {
            return getRitualBlockPoses().map(poi -> serverWorld.getBlockEntity(poi.getPos(), ManaStorageBlockEntity.TYPE)).filter(Optional::isPresent).map(Optional::get).filter(blockEntity -> blockEntity.getCachedState().get(ManaStorageBlock.MANA_LEVEL) > 0);
        }
        return Stream.empty();
    }

    public Stream<ItemStack> getAvailableItemStacks() {
        return this.getItemSacrificers().map(itemSacrificerBlockEntity -> itemSacrificerBlockEntity.storedStack);
    }


    @Override
    public void markRemoved() {
        this.holder.destroy();
        super.markRemoved();
    }

    public enum State {
        INACTIVE,
        READY,
        ACTIVE
    }
}
