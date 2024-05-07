package dev.louis.zauber.ritual;

import dev.louis.zauber.block.entity.RitualStoneBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

import java.util.Random;
import java.util.stream.Stream;

public class FailRitual extends Ritual {
    public Random rand = new Random();
    public FailRitual(World world, RitualStoneBlockEntity ritualStoneBlockEntity) {
        super(world, ritualStoneBlockEntity);
    }

    @Override
    public void tick() {
        if (rand.nextFloat() > 0.05) return;
        ritualStoneBlockEntity.getItemSacrificers().findAny().ifPresent(itemSacrificer -> {
            var pos = itemSacrificer.getPos();
            ((ServerWorld) itemSacrificer.getWorld()).spawnParticles(
                    ParticleTypes.DUST_PLUME,
                    pos.getX(),
                    pos.getY() + 1,
                    pos.getZ(),
                    1,
                    0,
                    0,
                    0,
                    0
            );
            itemSacrificer.setStoredStack(ItemStack.EMPTY);
        });
    }

    @Override
    public void onStart() {
        ((ServerWorld) this.ritualStoneBlockEntity.getWorld()).spawnParticles(
                ParticleTypes.DUST_PLUME,
                pos.getX(),
                pos.getY() + 1,
                pos.getZ(),
                10,
                0.1,
                0.1,
                0.1,
                0
        );
    }

    @Override
    public void finish() {

    }

    @Override
    public boolean shouldStop() {
        return rand.nextBoolean();
    }

    @Override
    public Stream<Position> getConnections() {
        return Stream.empty();
    }

    public static Ritual tryStart(World world, RitualStoneBlockEntity ritualStoneBlockEntity) {
        return new FailRitual(world, ritualStoneBlockEntity);
    }
}
