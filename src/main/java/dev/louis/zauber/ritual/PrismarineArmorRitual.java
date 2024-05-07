package dev.louis.zauber.ritual;

import dev.louis.zauber.block.entity.RitualStoneBlockEntity;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

import java.util.stream.Stream;

public class PrismarineArmorRitual extends Ritual {
    public PrismarineArmorRitual(World world, RitualStoneBlockEntity blockEntity) {
        super(world, blockEntity);
    }

    @Override
    public void tick() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void finish() {

    }

    @Override
    public boolean shouldStop() {
        return false;
    }

    @Override
    public Stream<Position> getConnections() {
        return Stream.empty();
    }
}
