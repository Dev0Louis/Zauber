package dev.louis.zauber.ritual;

import dev.louis.zauber.block.entity.RitualStoneBlockEntity;
import net.minecraft.world.World;

public class TestRitual extends Ritual implements ManaPullingRitual {
    public TestRitual(World world, RitualStoneBlockEntity ritualStoneBlockEntity) {
        super(world, ritualStoneBlockEntity);
    }

    public static Ritual tryStart(World world, RitualStoneBlockEntity ritualStoneBlockEntity) {
        return new TestRitual(world, ritualStoneBlockEntity);
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
}
