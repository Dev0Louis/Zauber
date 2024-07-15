package dev.louis.zauber.ritual;

import dev.louis.zauber.Zauber;
import dev.louis.zauber.block.entity.RitualStoneBlockEntity;
import dev.louis.zauber.ritual.heart.HeartOfDarknessRitual;
import dev.louis.zauber.ritual.heart.HeartOfTheIceRitual;
import dev.louis.zauber.ritual.heart.HeartOfTheSeaRitual;
import dev.louis.zauber.ritual.spell.HailSpellRitual;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
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
        RITUAL_STARTERS.add(TotemOfDarknessRitual::tryStart);
        RITUAL_STARTERS.add(TotemOfIceRitual::tryStart);
        RITUAL_STARTERS.add(TotemOfManaRitual::tryStart);
        RITUAL_STARTERS.add(ManaBowRitual::tryStart);
        RITUAL_STARTERS.add(HeartOfDarknessRitual::tryStart);
        RITUAL_STARTERS.add(MudifyRitual::tryStart);
        RITUAL_STARTERS.add(HailSpellRitual::tryStart);
        RITUAL_STARTERS.add(HeartOfTheIceRitual::tryStart);
        RITUAL_STARTERS.add(HeartOfTheSeaRitual::tryStart);
        RITUAL_STARTERS.add(TeleportToLodestoneRitual::tryStart);

        //TODO: Data drive this
        RITUAL_STARTERS.add(new SummonEntityRitual.Starter(EntityType.COW, Ingredient.ofItems(Items.BEEF)));
        RITUAL_STARTERS.add(new SummonEntityRitual.Starter(EntityType.PIG, Ingredient.ofItems(Items.PORKCHOP)));
        RITUAL_STARTERS.add(new SummonEntityRitual.Starter(EntityType.RABBIT, Ingredient.ofItems(Items.RABBIT)));
        RITUAL_STARTERS.add(new SummonEntityRitual.Starter(EntityType.SHEEP, Ingredient.ofItems(Items.MUTTON)));
        RITUAL_STARTERS.add(new SummonEntityRitual.Starter(EntityType.ZOMBIE_HORSE, Ingredient.ofItems(Items.ROTTEN_FLESH)));
        RITUAL_STARTERS.add(new SummonEntityRitual.Starter(EntityType.SPIDER, Ingredient.ofItems(Items.SPIDER_EYE)));
        RITUAL_STARTERS.add(new SummonEntityRitual.Starter(EntityType.SQUID, Ingredient.ofItems(Items.INK_SAC)));
        RITUAL_STARTERS.add(new SummonEntityRitual.Starter(EntityType.GLOW_SQUID, Ingredient.ofItems(Items.GLOW_INK_SAC)));
        RITUAL_STARTERS.add(new SummonEntityRitual.Starter(EntityType.CHICKEN, Ingredient.ofItems(Items.CHICKEN)));

        RITUAL_STARTERS.add(new SummonEntityRitual.Starter((world1, itemStack) -> {
            var cat = EntityType.CAT.create(world1);
            if (cat == null) {
                Zauber.LOGGER.error("THE CAT IS NULL; HOW WHAT THE FRICK?");
                throw new IllegalStateException();
            }

            if (itemStack.getName().contains(Text.of("diced"))) {
                var persianCatVariant = Registries.CAT_VARIANT.getEntry(CatVariant.PERSIAN);
                persianCatVariant.ifPresent(cat::setVariant);
                cat.setCustomName(Text.of("dicedpixels"));
            }

            return cat;
        }, Ingredient.ofItems(Items.STRING)));

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
