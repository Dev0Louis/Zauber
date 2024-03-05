package dev.louis.zauber.block.entity;

import dev.louis.zauber.block.ZauberBlocks;
import dev.louis.zauber.particle.ZauberParticleTypes;
import dev.louis.zauber.poi.ZauberPointOfInterestTypes;
import dev.louis.zauber.ritual.Ritual;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class RitualStoneBlockEntity extends BlockEntity {
    public static final BlockEntityType<RitualStoneBlockEntity> TYPE = BlockEntityType.Builder.create(RitualStoneBlockEntity::new, ZauberBlocks.RITUAL_STONE).build(null);
    @Nullable
    private Ritual ritual;
    private int ticksSinceRitualAttempt = 0;
    public List<BlockPos> itemSacrificer = new ArrayList<>();
    boolean hasInitialised = false;

    public RitualStoneBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE, pos, state);
    }


    public void init() {

    }

    public static void tick(World world, BlockPos blockPos, BlockState state, RitualStoneBlockEntity ritualStoneBlockEntity) {
        if(!ritualStoneBlockEntity.hasInitialised) ritualStoneBlockEntity.init();

        if(ritualStoneBlockEntity.ticksSinceRitualAttempt > 0) {
            ritualStoneBlockEntity.ticksSinceRitualAttempt--;
            return;
        }
        ritualStoneBlockEntity.spawnConnectionParticle();

        final var ritual = ritualStoneBlockEntity.ritual;
        if(ritual != null) {
            if(ritual.shouldStop()) {
                ritual.finish();
                ritualStoneBlockEntity.ritual = null;
                return;
            }

            ritual.baseTick();
        }
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

    public void onBlockClicked(PlayerEntity player) {
        if (this.ritual != null) return;
        Ritual.RITUALS.forEach(ritualStarter -> {
            if (this.ritual != null) return;
            this.ritual = ritualStarter.tryStart(world, this, player);
        });
        if (this.ritual != null) {
            var sound = this.ritual.getStartSound();
            var volume = this.ritual.getVolume();
            var pitch = -2;
            player.playSound(sound, SoundCategory.PLAYERS, volume, pitch);
            player.playSound(sound, volume, pitch);
        }
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

    public Stream<ItemStack> getAvailableItems() {
        if (world instanceof ServerWorld serverWorld) {
            return getRitualBlockPos().map(poi -> serverWorld.getBlockEntity(poi.getPos(), RitualItemSacrificerBlockEntity.TYPE)).filter(Optional::isPresent).map(Optional::get).filter(ritualItemSacrificerBlockEntity -> ritualItemSacrificerBlockEntity.storedStack != ItemStack.EMPTY).map(ritualItemSacrificerBlockEntity -> ritualItemSacrificerBlockEntity.storedStack);
        }
        return Stream.empty();
    }

    public void onRitualBlockPlaced(BlockPos pos) {
        itemSacrificer.add(pos);
    }

    public void onRitualBlockRemoved(BlockPos pos) {
        itemSacrificer.remove(pos);
    }
}
