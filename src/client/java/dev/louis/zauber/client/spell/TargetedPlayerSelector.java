package dev.louis.zauber.client.spell;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellType;
import dev.louis.zauber.Zauber;
import dev.louis.zauber.client.ZauberClient;
import dev.louis.zauber.config.ConfigManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.Optional;

public class TargetedPlayerSelector {
        private TargetedPlayerSelector() {}

        private static final MinecraftClient client = MinecraftClient.getInstance();
        static boolean hasRaycastRun = false;
        static PlayerEntity playerInView;

        public static void init() {
            ClientTickEvents.END_WORLD_TICK.register(client -> {
                TargetedPlayerSelector.calculatePlayerInView();
                TargetedPlayerSelector.hasRaycastRun = false;
            });
        }

        public static Optional<PlayerEntity> getPlayerInView() {
            if (!hasRaycastRun) TargetedPlayerSelector.calculatePlayerInView();
            return Optional.ofNullable(playerInView);
        }

        private static boolean shouldTargetingRun() {
            if(client.player == null) return false;
            for (SpellType<? extends Spell> spellType : Zauber.Spells.targetingSpells) {
                if (spellType.hasLearned(client.player))return true;
            }
            return false;
        }

        private static void calculatePlayerInView() {
            if (!shouldTargetingRun()) return;
            TargetedPlayerSelector.playerInView = null;

            if (client == null || client.getCameraEntity() == null) return;

            var pos = client.getCameraEntity().getEyePos();
            int divider = ConfigManager.getClientConfig().raycastScanPrecision();
            double i = 1.0 / divider;
            var x = client.getCameraEntity().getRotationVecClient().normalize().multiply(i);


            searchForPlayerInView:
            for (int y = 0; y < 24 * divider; ++y) {
                for (PlayerEntity targetedPlayer : getPlayersWithoutSelf()) {
                    if (targetedPlayer.getBoundingBox().expand(0.3).contains(pos)) {
                        if (ZauberClient.isPlayerTargetable(targetedPlayer)) {
                            TargetedPlayerSelector.playerInView = targetedPlayer;
                            break searchForPlayerInView;
                        }
                    }
                    pos = pos.add(x);
                }
            }
            TargetedPlayerSelector.hasRaycastRun = true;
        }

        private static Iterable<? extends PlayerEntity> getPlayersWithoutSelf() {
            final PlayerEntity clientPlayer = MinecraftClient.getInstance().player;
            var players = new ArrayList<>(clientPlayer.getWorld().getPlayers());
            players.remove(clientPlayer);
            return players;
        }
    }