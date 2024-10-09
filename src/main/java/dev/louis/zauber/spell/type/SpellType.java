package dev.louis.zauber.spell.type;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.zauber.Zauber;
import dev.louis.zauber.item.SpellBookItem;
import dev.louis.zauber.spell.*;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;

public record SpellType<S extends Spell<?>> (SpellFactory<S> factory) {
    public static RegistryKey<Registry<SpellType<?>>> REGISTRY_KEY =
            RegistryKey.ofRegistry(Identifier.of(Zauber.MOD_ID, "spell_types"));
    public static SimpleRegistry<SpellType<?>> REGISTRY =
            FabricRegistryBuilder.createSimple(REGISTRY_KEY).buildAndRegister();

    public static SpellType<ArrowSpell> ARROW = registerSimple("arrow", ArrowSpell::new);
    public static SpellType<JuggernautSpell> JUGGERNAUT = registerSimple("juggernaut", JuggernautSpell::new);
    public static SpellType<PullSpell> PULL = registerSimple("pull", PullSpell::new);
    public static SpellType<PushSpell> PUSH = registerSimple("push", PushSpell::new);
    public static SpellType<RewindSpell> REWIND = registerSimple("rewind", RewindSpell::new);
    public static SpellType<SuicideSpell> SUICIDE = registerSimple("suicide", SuicideSpell::new);
    public static SpellType<TeleportSpell> TELEPORT = registerSimple("teleport", TeleportSpell::new);
    public static SpellType<SupernovaSpell> SUPERNOVA = registerSimple("supernova", SupernovaSpell::new);
    public static SpellType<AreaEffectSpell> FIRE = registerSimple("fire", FireSpell::new);
    public static SpellType<AreaEffectSpell> ICE = registerSimple("ice", IceSpell::new);
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

    public static <S extends Spell<?>> SpellType<S> registerSimple(String spellName, SimpleSpellFactory<S> spellFactory) {
        return register(spellName, spellFactory);
    }

    public static <S extends Spell<?>> SpellType<S> register(String spellName, SpellFactory<S> spellFactory) {
        SpellType<S> spellType = Registry.register(REGISTRY, Identifier.of(Zauber.MOD_ID, spellName), new SpellType<>(spellFactory));
        //ZAUBER_SPELLS.add(spellType);
        //SPELLBOOKS.add(SpellBookItem.createSpellBook(spellType));
        return spellType;
    }

}
