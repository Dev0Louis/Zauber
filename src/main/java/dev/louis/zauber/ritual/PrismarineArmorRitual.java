package dev.louis.zauber.ritual;

import dev.louis.zauber.block.entity.RitualStoneBlockEntity;
import net.minecraft.world.World;

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
}
