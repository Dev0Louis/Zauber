package dev.louis.zauber.block.entity;

import dev.louis.zauber.block.ZauberBlocks;
import dev.louis.zauber.particle.ZauberParticleTypes;
import dev.louis.zauber.ritual.Ritual;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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
        Box box = Box.of(this.pos.toCenterPos(), 10, 10, 10);
        this.itemSacrificer.clear();
        System.out.println("INITING");
        BlockPos.stream(box).forEach(blockPos -> {
            world.getBlockEntity(blockPos, RitualItemSacrificerBlockEntity.TYPE).ifPresent((blockEntity) -> itemSacrificer.add(blockEntity.getPos()));
        });
        hasInitialised = true;
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
                ritualStoneBlockEntity.ritual = null;
                ritual.finish();
                return;
            }

            ritual.baseTick();
        }
    }

    protected void spawnConnectionParticle() {
        final Vec3d ritualPos = pos.toCenterPos();
        if(world.getTime() % 15 == 0) {
            for (BlockPos blockPos : itemSacrificer) {

                final int steps = 10;
                final Vec3d sacrificerPos = blockPos.toCenterPos();
                for (int i = 0; i < steps; i++) {
                    //System.out.println(i);
                    var delta = (double) i / steps;
                    delta = Math.max(delta, 0.2);
                    delta = Math.min(delta, 0.8);
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
                            0.,
                            0.,
                            0.,
                            0.0
                    );
                }
            }
        }

    }

    public void onBlockClicked(PlayerEntity player) {
        if (this.ritual != null) return;
        Ritual.RITUALS.forEach(ritualStarter -> {
            if (this.ritual != null) return;
            this.ritual = ritualStarter.tryStart(world, pos, player);
        });
    }

    public void onRitualBlockPlaced(BlockPos pos) {
        itemSacrificer.add(pos);
    }

    public void onRitualBlockRemoved(BlockPos pos) {
        itemSacrificer.remove(pos);
    }
}
