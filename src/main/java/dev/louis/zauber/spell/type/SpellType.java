package dev.louis.zauber.spell.type;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.zauber.Zauber;
import dev.louis.zauber.item.SpellBookItem;
import dev.louis.zauber.spell.*;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;

public record SpellType<C, S extends Spell<C>> (SpellFactory<C, S> factory) {
    static RegistryKey<Registry<SpellType<?, ?>>> REGISTRY_KEY =
            RegistryKey.ofRegistry(Identifier.of(Zauber.MOD_ID, "spell_types"));
    static SimpleRegistry<SpellType<?, ?>> REGISTRY =
            FabricRegistryBuilder.createSimple(REGISTRY_KEY).buildAndRegister();

    public static SpellType<LivingEntity, ArrowSpell> ARROW = registerSimple("arrow", ArrowSpell::new);
    public static SpellType<PlayerEntity, JuggernautSpell> JUGGERNAUT = registerSimple("juggernaut", JuggernautSpell::new);
    public static SpellType<LivingEntity, PullSpell> PULL = registerSimple("pull", PullSpell::new);
    public static SpellType<LivingEntity, PushSpell> PUSH = registerSimple("push", PushSpell::new);
    public static SpellType<LivingEntity, RewindSpell> REWIND = registerSimple("rewind", RewindSpell::new);
    public static SpellType<LivingEntity, SuicideSpell> SUICIDE = registerSimple("suicide", SuicideSpell::new);
    public static SpellType<LivingEntity, TeleportSpell> TELEPORT = registerSimple("teleport", TeleportSpell::new, 2);
    public static SpellType<LivingEntity, SupernovaSpell> SUPERNOVA = registerSimple("supernova", SupernovaSpell::new, 20);
    public static SpellType<LivingEntity, FireSpell> FIRE = registerSimple("fire", FireSpell::new, 2);
    public static SpellType<IceSpell> ICE = registerSimple("ice", IceSpell::new, 2);
    public static SpellType<HailStormSpell> HAIL_STORM = registerParallelCasting("hail_storm", HailStormSpell::new, 3);
    public static SpellType<WindExpelSpell> WIND_EXPEL = registerSimple("wind_expel", WindExpelSpell::new, 5);
    public static SpellType<SproutSpell> SPROUT = registerSimple("sprout", SproutSpell::new, 2);
    public static SpellType<DashSpell> DASH = registerSimple("dash", DashSpell::new, 4);
    public static SpellType<VengeanceSpell> VENGEANCE = registerSimple("vengeance", VengeanceSpell::new, 2);
    public static SpellType<ConjoureFangSpell> CONJOURE_FANG = registerSimple("conjoure_fang", ConjoureFangSpell::new, 2);


    public static <C, S extends Spell<C>> SpellType<C, S> registerNoLearning(String spellName, SpellFactory<S> spellFactory, int mana) {
        return SpellType.registerSimple(
                Identifier.of(MOD_ID, spellName),
                SpellType.Builder.create(spellFactory, mana).needsLearning(false)
        );
    }

    public static <T extends Spell> SpellType<T> registerParallelCasting(String spellName, SpellType.SpellFactory<T> spellFactory, int mana) {
        SpellType<T> spellType = SpellType.registerSimple(Identifier.of(MOD_ID, spellName), SpellType.Builder.create(spellFactory, mana).parallelCast());
        ZAUBER_SPELLS.add(spellType);
        SPELLBOOKS.add(SpellBookItem.createSpellBook(spellType));
        return spellType;
    }

    public static <C, S extends Spell<C>> SpellType<C, S> registerSimple(String spellName, SimpleSpellFactory<C, S> spellFactory) {
        return register(spellName, spellFactory);
    }

    public static <C, S extends Spell<C>> SpellType<C, S> register(String spellName, SpellFactory<C, S> spellFactory) {
        SpellType<C, S> spellType = Registry.register(REGISTRY, Identifier.of(Zauber.MOD_ID, spellName), new SpellType<>(spellFactory));
        //ZAUBER_SPELLS.add(spellType);
        //SPELLBOOKS.add(SpellBookItem.createSpellBook(spellType));
        return spellType;
    }

}
