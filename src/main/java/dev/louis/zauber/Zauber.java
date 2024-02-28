package dev.louis.zauber;

import com.mojang.logging.LogUtils;
import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellType;
import dev.louis.nebula.api.spell.SpellType.Castability;
import dev.louis.zauber.block.ZauberBlocks;
import dev.louis.zauber.config.ConfigManager;
import dev.louis.zauber.entity.ManaHorseEntity;
import dev.louis.zauber.entity.SpellArrowEntity;
import dev.louis.zauber.items.ZauberItems;
import dev.louis.zauber.mana.effect.ManaEffects;
import dev.louis.zauber.networking.ICanHasZauberPayload;
import dev.louis.zauber.networking.OptionSyncCompletePacket;
import dev.louis.zauber.networking.OptionSyncPacket;
import dev.louis.zauber.networking.OptionSyncTask;
import dev.louis.zauber.particle.ZauberParticleTypes;
import dev.louis.zauber.recipe.ZauberRecipes;
import dev.louis.zauber.spell.*;
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import eu.pb4.polymer.networking.api.PolymerNetworking;
import eu.pb4.polymer.networking.api.server.PolymerServerNetworking;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.List;

public class Zauber implements ModInitializer {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "zauber";
    public static final int POLYMER_NETWORK_VERSION = 1;
    public static final Identifier HAS_CLIENT_MODS = Identifier.of(MOD_ID, "has_spell_table");
    //Hacky way to get Client data
    public static PlayerViewGetter PLAYER_VIEWER_GETTER;

    @Override
    public void onInitialize() {
        ConfigManager.loadServerConfig();

        ServerConfigurationConnectionEvents.CONFIGURE.register((handler, server) -> {
            if (ServerConfigurationNetworking.canSend(handler, OptionSyncPacket.TYPE.getId())) {
                handler.addTask(new OptionSyncTask());
            }
        });

        ServerConfigurationNetworking.registerGlobalReceiver(OptionSyncCompletePacket.TYPE, (packet, networkHandler, responseSender) -> {
            networkHandler.completeTask(OptionSyncTask.KEY);
        });

        PolymerNetworking.registerCommonPayload(Zauber.HAS_CLIENT_MODS, POLYMER_NETWORK_VERSION, ICanHasZauberPayload::read);

        Spells.init();
        ZauberRecipes.init();
        ZauberItems.init();
        ZauberBlocks.init();

        registerEntity("spell_arrow", SpellArrowEntity.TYPE);
        //FabricDefaultAttributeRegistry.register(SpellArrowEntity.TYPE, SpellArrowEntity.createMobAttributes());
        registerEntity("mana_horse", ManaHorseEntity.TYPE);
        FabricDefaultAttributeRegistry.register(ManaHorseEntity.TYPE, ManaHorseEntity.createBaseHorseAttributes());
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(MOD_ID, "mana_rune"), ZauberParticleTypes.MANA_RUNE);
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(MOD_ID, "mana_explosion"), ZauberParticleTypes.MANA_EXPLOSION);
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(MOD_ID, "mana_explosion_emitter"), ZauberParticleTypes.MANA_EXPLOSION_EMITTER);

        ManaEffects.init();
    }

    public static <T extends Entity> void registerEntity(String path, EntityType<T> type) {
        Registry.register(Registries.ENTITY_TYPE, Identifier.of(Zauber.MOD_ID, path), type);
        PolymerEntityUtils.registerType(type);
    }

    public static class Spells {
        public static List<SpellType<?>> targetingSpells;
        private static final Castability TARGETING_CASTABILITY =
                Castability.DEFAULT.and((spellType, caster) -> {
                    if(caster.getWorld().isClient()) {
                        var playerInView = PLAYER_VIEWER_GETTER.getPlayerInView();
                        return playerInView.isPresent() && caster.distanceTo(playerInView.get()) < ConfigManager.getServerConfig().targetingDistance();
                    }
                    return true;
                });
        private static final Castability MANA_HORSE_CASTABILITY =
                Castability.DEFAULT.and((spellType, caster) -> {
                    if(caster.getWorld().isClient()) {
                        var playerInView = PLAYER_VIEWER_GETTER.getPlayerInView();
                        return playerInView.isPresent() && caster.distanceTo(playerInView.get()) < ConfigManager.getServerConfig().targetingDistance();
                    }
                    return true;
                });

        public static SpellType<ArrowSpell> ARROW = register("arrow", ArrowSpell::new, 2);
        public static SpellType<JuggernautSpell> JUGGERNAUT = register("juggernaut", JuggernautSpell::new, 20);
        public static SpellType<PullSpell> PULL = register("pull", PullSpell::new, 2, TARGETING_CASTABILITY);
        public static SpellType<PushSpell> PUSH = register("push", PushSpell::new, 2, TARGETING_CASTABILITY);
        public static SpellType<RewindSpell> REWIND = register("rewind", RewindSpell::new, 5);
        public static SpellType<SuicideSpell> SUICIDE = register("suicide", SuicideSpell::new, 1);
        public static SpellType<TeleportSpell> TELEPORT = register("teleport", TeleportSpell::new, 5, TARGETING_CASTABILITY);
        public static SpellType<SupernovaSpell> SUPERNOVA = register("supernova", SupernovaSpell::new, 20);
        public static SpellType<FireSpell> FIRE = register("fire", FireSpell::new, 2);
        public static SpellType<IceSpell> ICE = register("ice", IceSpell::new, 2);
        public static SpellType<WindExpelSpell> WIND_EXPEL = register("wind_expel", WindExpelSpell::new, 4);
        public static SpellType<SproutSpell> SPROUT = register("sprout", SproutSpell::new, 2);
        public static SpellType<DashSpell> DASH = register("dash", DashSpell::new, 4);
        public static SpellType<ManaHorseSpell> MANA_HORSE = registerManaHorse("mana_horse", ManaHorseSpell::new, 4);


        public static <T extends Spell> SpellType<T> registerManaHorse(String spellName, SpellType.SpellFactory<T> spellFactory, int mana) {
            return SpellType.register(
                    Identifier.of(MOD_ID, spellName),
                    SpellType.Builder.create(spellFactory, mana)
                            .castability(Castability.DEFAULT.and((spellType, playerEntity) -> !playerEntity.hasVehicle()))
                            .needsLearning(false)
            );
        }

        public static <T extends Spell> SpellType<T> register(String spellName, SpellType.SpellFactory<T> spellFactory, int mana) {
            return SpellType.register(Identifier.of(MOD_ID, spellName),SpellType.Builder.create(spellFactory, mana));
        }

        public static <T extends Spell> SpellType<T> register(String spellName, SpellType.SpellFactory<T> spellFactory, int mana, Castability castability) {
            return SpellType.register(Identifier.of(MOD_ID, spellName),SpellType.Builder.create(spellFactory, mana).castability(castability));
        }

        public static void init() {
            targetingSpells = List.of(Spells.PULL, Spells.PUSH, Spells.TELEPORT);
        }
    }

    public static boolean isClientModded(@Nullable ServerPlayerEntity player) {
        if(player != null && player.networkHandler != null) {
            var version = PolymerServerNetworking.getSupportedVersion(player.networkHandler, Zauber.HAS_CLIENT_MODS);
            return version == POLYMER_NETWORK_VERSION;
        }
        return false;
    }

}
