package dev.louis.zauber.ritual;

import dev.louis.zauber.block.entity.RitualStoneBlockEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public abstract class Ritual {
    public static final List<Starter> RITUAL_STARTERS = new ArrayList<>();

    protected final World world;
    protected final BlockPos pos;
    protected final RitualStoneBlockEntity ritualStoneBlockEntity;
    public int age;

    public Ritual(World world, RitualStoneBlockEntity ritualStoneBlockEntity) {
        this.world = world;
        this.pos = ritualStoneBlockEntity.getPos();
        this.ritualStoneBlockEntity = ritualStoneBlockEntity;
    }

    public static void init() {
        //The order is important as the Rituals are checked in order.
        RITUAL_STARTERS.add(HorseRitual::tryStart);
    }


    public abstract void tick();

    public abstract void onStart();

    public abstract void finish();

    public abstract boolean shouldStop();

    public SoundEvent getStartSound() {
        return SoundEvents.ENTITY_ARROW_HIT_PLAYER;
    }

    public float getPitch() {
        return 1;
    }

    public float getVolume() {
        return 1;
    }

    public interface Starter {
        Ritual tryStart(World world, RitualStoneBlockEntity ritualStoneBlockEntity);
    }

}
