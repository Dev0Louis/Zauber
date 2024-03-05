package dev.louis.zauber.block.entity;

import com.google.common.collect.ImmutableList;
import dev.louis.zauber.block.ZauberBlocks;
import dev.louis.zauber.particle.ZauberParticleTypes;
import dev.louis.zauber.poi.ZauberPointOfInterestTypes;
import dev.louis.zauber.ritual.Ritual;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

public class RitualStoneBlockEntity extends BlockEntity {
    public static final BlockEntityType<RitualStoneBlockEntity> TYPE = BlockEntityType.Builder.create(RitualStoneBlockEntity::new, ZauberBlocks.RITUAL_STONE).build(null);
    @Nullable
    private Ritual ritual;
    private Collection<BlockPos> itemSacrificers = new ArrayList<>();
    private final Collection<ItemStack> collectedItems = new ArrayList<>();
    protected final Random random = Random.create();
    private int age = 0;
    boolean hasInitialised = false;
    private boolean isRitualActive;
    private States state;

    public RitualStoneBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE, pos, state);
    }


    public void init() {

    }

    public void tick(World world, BlockPos blockPos, BlockState stat) {
        if(!this.hasInitialised) this.init();
        age++;
        this.spawnConnectionParticle();

        if(this.state == States.COLLECTING) {
            if (this.age % 20 == 0) {
                var optional = this.itemSacrificers.stream()
                        .map(pos1 -> world.getBlockEntity(pos1, RitualItemSacrificerBlockEntity.TYPE)).filter(Optional::isPresent).map(Optional::get).filter(ritualItemSacrificerBlockEntity -> !ritualItemSacrificerBlockEntity.storedStack.isEmpty()).findAny();
                optional.ifPresentOrElse(ritualItemSacrificerBlockEntity -> {
                    collectedItems.add(ritualItemSacrificerBlockEntity.storedStack);
                    ritualItemSacrificerBlockEntity.setStoredStack(ItemStack.EMPTY);
                }, () -> this.state = States.INACTIVE);
            }
            return;
        } else if (this.state == States.PREPARE) {
            Ritual.RITUALS.forEach(ritualStarter -> {
                if (this.ritual != null) return;
                this.ritual = ritualStarter.tryStart(world, this);
            });
        }


        if(ritual != null) {
            if(ritual.shouldStop()) {
                this.state = States.INACTIVE;
                ritual.finish();
                ritual = null;
                return;
            }

            ritual.baseTick();
        }
    }

    public void onBlockClicked(PlayerEntity player) {
        if (this.state == States.ACTIVE) return;

        if (player.isSneaking()) {
            dropCollectedItems();
        }
        System.out.println(this.collectedItems.size());
        this.isRitualActive = true;
        this.itemSacrificers = getItemSacrificersPos().toList();
        this.state = this.collectedItems.isEmpty() ? States.COLLECTING : States.PREPARE;
    }

    private void dropCollectedItems() {
        Vec3d vec3d = this.getPos().up().toCenterPos();
        this.collectedItems.forEach(itemStack -> {
            double velocityX = MathHelper.nextBetween(this.random, -0.2F, 0.2F);
            double velocityY = MathHelper.nextBetween(this.random, 0.3F, 0.7F);
            double velocityZ = MathHelper.nextBetween(this.random, -0.2F, 0.2F);
            ItemEntity itemEntity = new ItemEntity(this.getWorld(), vec3d.getX(), vec3d.getY(), vec3d.getZ(), itemStack, velocityX, velocityY, velocityZ);
            this.getWorld().spawnEntity(itemEntity);
        });
        this.collectedItems.clear();
    }

    protected void spawnConnectionParticle() {
        final Vec3d ritualPos = pos.toCenterPos();
        if(world.getTime() % 15 == 0) {
            getRitualBlockPos().forEach(pointOfInterest -> {
                final int steps = 10;
                final Vec3d sacrificerPos = pointOfInterest.getPos().toCenterPos();
                for (int i = 0; i < steps; i++) {
                    var delta = (double) i / steps;
                    delta = (delta * 0.9) + 0.1;
                    var x = MathHelper.lerp(delta, sacrificerPos.x, ritualPos.x);
                    var y = MathHelper.lerp(delta, sacrificerPos.y, ritualPos.y);
                    var z = MathHelper.lerp(delta, sacrificerPos.z, ritualPos.z);
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

    public Collection<ItemStack> getCollectedItems() {
        return ImmutableList.copyOf(collectedItems);
    }

    public boolean removeCollectedItem(ItemStack itemStack) {
        return this.collectedItems.remove(itemStack);
    }

    public Stream<PointOfInterest> getRitualBlockPos() {
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

    public Stream<BlockPos> getItemSacrificersPos() {
        if (world instanceof ServerWorld serverWorld) {
            return getRitualBlockPos().map(poi -> serverWorld.getBlockEntity(poi.getPos(), RitualItemSacrificerBlockEntity.TYPE)).filter(Optional::isPresent).map(Optional::get).filter(blockEntity -> blockEntity.storedStack != ItemStack.EMPTY).map(BlockEntity::getPos);
        }
        return Stream.empty();
    }

    public Stream<RitualItemSacrificerBlockEntity> getItemSacrificers() {
        if (world instanceof ServerWorld serverWorld) {
            return getItemSacrificersPos().map(pos1 -> serverWorld.getBlockEntity(pos1, RitualItemSacrificerBlockEntity.TYPE)).filter(Optional::isPresent).map(Optional::get);
        }
        return Stream.empty();
    }

    public enum States {
        INACTIVE,
        COLLECTING,
        PREPARE,
        ACTIVE
    }
}
