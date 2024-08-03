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
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public abstract class Ritual {
    public static final HashMap<Identifier, Starter> RITUAL_STARTERS = new HashMap<>();

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
        register("horse_ritual", HorseRitual::tryStart);
        register("totem_of_darkness", TotemOfDarknessRitual::tryStart);
        register("totem_of_ice", TotemOfIceRitual::tryStart);
        register("totem_of_mana", TotemOfManaRitual::tryStart);
        register("mana_bow", ManaBowRitual::tryStart);
        register("heart_of_darkness", HeartOfDarknessRitual::tryStart);
        register("mudify", MudifyRitual::tryStart);
        register("hail_spell", HailSpellRitual::tryStart);
        register("heart_of_the_ice", HeartOfTheIceRitual::tryStart);
        register("heart_of_the_sea", HeartOfTheSeaRitual::tryStart);
        register("teleport_to_lodestone", TeleportToLodestoneRitual::tryStart);

        //TODO: Data drive this
        register("summon_cow", new SummonEntityRitual.Starter(EntityType.COW, Ingredient.ofItems(Items.BEEF)));
        register("summon_pig", new SummonEntityRitual.Starter(EntityType.PIG, Ingredient.ofItems(Items.PORKCHOP)));
        register("summon_rabbit", new SummonEntityRitual.Starter(EntityType.RABBIT, Ingredient.ofItems(Items.RABBIT)));
        register("summon_sheep", new SummonEntityRitual.Starter(EntityType.SHEEP, Ingredient.ofItems(Items.MUTTON)));
        register("summon_zombie_horse", new SummonEntityRitual.Starter(EntityType.ZOMBIE_HORSE, Ingredient.ofItems(Items.ROTTEN_FLESH)));
        register("summon_spider", new SummonEntityRitual.Starter(EntityType.SPIDER, Ingredient.ofItems(Items.SPIDER_EYE)));
        register("summon_squid", new SummonEntityRitual.Starter(EntityType.SQUID, Ingredient.ofItems(Items.INK_SAC)));
        register("summon_glow_squid", new SummonEntityRitual.Starter(EntityType.GLOW_SQUID, Ingredient.ofItems(Items.GLOW_INK_SAC)));
        register("summon_chicken", new SummonEntityRitual.Starter(EntityType.CHICKEN, Ingredient.ofItems(Items.CHICKEN)));

        register("summon_cat", new SummonEntityRitual.Starter((world1, itemStack) -> {
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

        register("smelting", SmeltingRitual::tryStart);
    }


    public static void register(String id, Starter starter) {
        register(Identifier.of(Zauber.MOD_ID, id), starter);
    }


    public static void register(Identifier id, Starter starter) {
        if (RITUAL_STARTERS.containsKey(id)) throw new IllegalStateException("TRIED DOUBLE REGISTRATION OF " + id + "!");
        RITUAL_STARTERS.put(id, starter);
    }

    public abstract void tick();

    public abstract void onStart();

    public abstract void finish();

    public abstract boolean shouldStop();

    public SoundEvent getStartSound() {
        return SoundEvents.ENTITY_ARROW_HIT_PLAYER;
    }

    public Stream<Position> getConnections() {
        return Stream.empty();
    };

    public interface Starter {
        Ritual tryStart(World world, RitualStoneBlockEntity ritualStoneBlockEntity);
    }

}
