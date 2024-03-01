package dev.louis.zauber.ritual;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public abstract class Ritual {
    public static final List<Starter> RITUALS = new ArrayList<>();

    protected World world;
    protected BlockPos pos;
    protected LivingEntity initiator;
    protected int age;

    public Ritual(World world, BlockPos pos, LivingEntity initiator) {
        this.world = world;
        this.pos = pos;
        this.initiator = initiator;
    }

    public static void init() {
        //The order is important as the Rituals are checked in order.
        RITUALS.add(HorseRitual::tryStart);
    }

    public final void baseTick() {
        this.age++;
        this.tick();
    }

    public abstract void tick();

    public abstract void start();

    public abstract void finish();

    public abstract boolean shouldStop();

    public interface Starter {
        Ritual tryStart(World world, BlockPos ritualStonePos, LivingEntity initiator);
    }

}
