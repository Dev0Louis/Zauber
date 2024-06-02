package dev.louis.zauber.ritual;

import dev.louis.zauber.block.entity.RitualStoneBlockEntity;
import dev.louis.zauber.ritual.entity.SummonEntityRitual;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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
        RITUAL_STARTERS.add(HeartOfDarknessRitual::tryStart);
        RITUAL_STARTERS.add(MudifyRitual::tryStart);
        RITUAL_STARTERS.add(HeartOfTheSeaRitual::tryStart);
        RITUAL_STARTERS.add(TeleportToLodestoneRitual::tryStart);

        //TODO: Data drive this
        RITUAL_STARTERS.add(new SummonEntityRitual.Starter(EntityType.COW, Ingredient.ofItems(Items.PORKCHOP)));
        RITUAL_STARTERS.add(new SummonEntityRitual.Starter(EntityType.PIG, Ingredient.ofItems(Items.PORKCHOP)));
        RITUAL_STARTERS.add(new SummonEntityRitual.Starter(EntityType.RABBIT, Ingredient.ofItems(Items.RABBIT)));
        RITUAL_STARTERS.add(new SummonEntityRitual.Starter(EntityType.SHEEP, Ingredient.ofItems(Items.MUTTON)));
        RITUAL_STARTERS.add(new SummonEntityRitual.Starter(EntityType.ZOMBIE_HORSE, Ingredient.ofItems(Items.ROTTEN_FLESH)));
        RITUAL_STARTERS.add(new SummonEntityRitual.Starter(EntityType.SPIDER, Ingredient.ofItems(Items.SPIDER_EYE)));
        RITUAL_STARTERS.add(new SummonEntityRitual.Starter(EntityType.SQUID, Ingredient.ofItems(Items.INK_SAC)));
        RITUAL_STARTERS.add(new SummonEntityRitual.Starter(EntityType.GLOW_SQUID, Ingredient.ofItems(Items.GLOW_INK_SAC)));

        RITUAL_STARTERS.add(SmeltingRitual::tryStart);


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

    public Stream<Position> getConnections() {
        return Stream.empty();
    };

    public interface Starter {
        Ritual tryStart(World world, RitualStoneBlockEntity ritualStoneBlockEntity);
    }

}
