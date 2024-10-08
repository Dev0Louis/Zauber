package dev.louis.zauber.ritual.mana;

import dev.louis.zauber.block.ManaCauldron;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.function.IntConsumer;

/**
 * The Amount of mana referenced
 */
public class ManaReference {
    private int mana;
    private final IntConsumer applier;
    private final World world;
    private final BlockPos source;
    private boolean invalid;

    /**
     * @param source the source where this mana reference came from
     */
    public ManaReference(int mana, World world, BlockPos source) {
        this(mana, world, source, (manaToPull) -> {
            var newMana = mana - manaToPull;
            if (newMana == 0) {
                world.setBlockState(source, Blocks.CAULDRON.getDefaultState());
            } else {
                world.setBlockState(source, world.getBlockState(source).with(ManaCauldron.MANA_LEVEL, newMana));
            }
        });
    }

    /**
     * @param applier the runnable to apply the mana being pulled out of the reference
     * @param source  the source where this mana reference came from
     */
    public ManaReference(int mana, World world, BlockPos source, IntConsumer applier) {
        this.mana = mana;
        this.applier = applier;
        this.world = world;
        this.source = source;
    }

    public int mana() {
        this.check();
        return invalid ? 0 : mana;
    }

    public BlockPos source() {
        return source;
    }

    public void apply() {
        this.check();
        if (invalid) throw new IllegalStateException("Tried pull mana from an invalid ManaReference.");
        this.invalidate();
        applier.accept(mana);
    }

    public void check() {
        var state = world.getBlockState(source);
        if (state.contains(ManaCauldron.MANA_LEVEL)) {
            var mana = state.get(ManaCauldron.MANA_LEVEL);
            if (mana < this.mana) this.invalidate();
        } else {
            this.invalidate();
        }
    }

    public boolean isInvalid() {
        return this.invalid;
    }

    public void invalidate() {
        this.invalid = true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mana, applier, source, invalid);
    }

    @Override
    public String toString() {
        return "ManaReference[" +
                "invalid=" + invalid + ", " +
                "mana=" + mana + ", " +
                "puller=" + applier + ", " +
                "source=" + source + ']';
    }

}
