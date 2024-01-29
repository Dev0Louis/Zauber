package dev.louis.zauber;

import com.mojang.logging.LogUtils;
import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellType;
import dev.louis.nebula.api.spell.SpellType.Castability;
import dev.louis.zauber.blocks.ZauberBlocks;
import dev.louis.zauber.config.ZauberConfig;
import dev.louis.zauber.items.ZauberItems;
import dev.louis.zauber.mana.effect.ManaEffects;
import dev.louis.zauber.networking.ICanHasZauberPayload;
import dev.louis.zauber.networking.OptionSyncCompletePacket;
import dev.louis.zauber.networking.OptionSyncPacket;
import dev.louis.zauber.networking.OptionSyncTask;
import dev.louis.zauber.recipe.ModRecipes;
import dev.louis.zauber.spell.*;
import eu.pb4.polymer.networking.api.PolymerNetworking;
import eu.pb4.polymer.networking.api.server.PolymerServerNetworking;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.List;

public class Zauber implements ModInitializer {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "zauber";
    public static final int VERSION = 1;
    public static final Identifier HAS_CLIENT_MODS = Identifier.of(MOD_ID, "has_spell_table");

    @Override
    public void onInitialize() {
        ZauberConfig.init();
        ServerConfigurationConnectionEvents.CONFIGURE.register((handler, server) -> {
            if (ServerConfigurationNetworking.canSend(handler, OptionSyncPacket.TYPE.getId())) {
                // Tasks are processed in order.
                handler.addTask(new OptionSyncTask());
            }
        });

        ServerConfigurationNetworking.registerGlobalReceiver(OptionSyncCompletePacket.TYPE, (packet, networkHandler, responseSender) -> {
            networkHandler.completeTask(OptionSyncTask.KEY);
        });

        ModRecipes.init();
        PolymerNetworking.registerCommonPayload(Zauber.HAS_CLIENT_MODS, 0, ICanHasZauberPayload::read);

        Spells.init();
        ZauberItems.init();
        ZauberBlocks.init();
        ManaEffects.init();
    }

    public static class Spells {
        public static List<SpellType<?>> targetingSpells;
        public static final Castability TARGETING =
                Castability.DEFAULT.and((spellType, caster) -> {
                    var playerInView = ZauberClient.getPlayerInView();
                    return playerInView.isPresent() && caster.distanceTo(playerInView.get()) < ZauberConfig.getSyncedTargetingDistance();
                });

        public static SpellType<ArrowSpell> ARROW = register("arrow", ArrowSpell::new, 2);
        public static SpellType<JuggernautSpell> JUGGERNAUT = register("juggernaut", JuggernautSpell::new, 20);
        public static SpellType<PullSpell> PULL = register("pull", PullSpell::new, 2, TARGETING);
        public static SpellType<PushSpell> PUSH = register("push", PushSpell::new, 2, TARGETING);
        public static SpellType<RewindSpell> REWIND = register("rewind", RewindSpell::new, 5);
        public static SpellType<SuicideSpell> SUICIDE = register("suicide", SuicideSpell::new, 1);
        public static SpellType<TeleportSpell> TELEPORT = register("teleport", TeleportSpell::new, 5, TARGETING);
        public static SpellType<SupernovaSpell> SUPERNOVA = register("supernova", SupernovaSpell::new, 20);
        public static SpellType<FireSpell> FIRE = register("fire", FireSpell::new, 2);
        public static SpellType<IceSpell> ICE = register("ice", IceSpell::new, 2);
        public static SpellType<WindExpelSpell> WIND_EXPEL = register("wind_expel", WindExpelSpell::new, 4);
        public static SpellType<SproutSpell> SPROUT = register("sprout", SproutSpell::new, 2);
        public static SpellType<DashSpell> DASH = register("dash", DashSpell::new, 4);

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

    public static boolean isClientVanilla(@Nullable ServerPlayerEntity player) {
        if(player != null && player.networkHandler != null) {
            var version = PolymerServerNetworking.getSupportedVersion(player.networkHandler, Zauber.HAS_CLIENT_MODS);
            return version == -1;
        }
        return false;
    }

}
