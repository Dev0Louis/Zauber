package dev.louis.zauber.spell;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellType;
import dev.louis.zauber.Zauber;
import dev.louis.zauber.ZauberClient;
import dev.louis.zauber.config.ConfigManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Optional;

public abstract class TargetingSpell extends Spell {
    private Integer entityId;

    public TargetingSpell(SpellType<? extends Spell> spellType) {
        super(spellType);
    }

    @Override
    public void setCaster(PlayerEntity caster) {
        if(caster.getWorld().isClient()) {
           castedOn(ZauberClient.getPlayerInView().orElse(null));
        }
        super.setCaster(caster);
    }

    @Override
    public void cast() {

    }

    public void castedOn(Entity castedOn) {
        if(castedOn == null)return;
        this.entityId = castedOn.getId();
    }

    @Nullable
    public Entity castedOn() {
        if(entityId == null)return null;
        return getCaster().getWorld().getEntityById(entityId);
    }

    @Override
    public PacketByteBuf writeBuf(PacketByteBuf buf) {
        super.writeBuf(buf);
        Optional<Integer> optionalInteger = castedOn() != null ? Optional.of(castedOn().getId()) : Optional.empty();
        buf.writeOptional(optionalInteger, PacketByteBuf::writeVarInt);
        return buf;
    }

    @Override
    public PacketByteBuf readBuf(PacketByteBuf buf) {
        super.readBuf(buf);
        Optional<Integer> o = buf.readOptional(PacketByteBuf::readVarInt);
        o.ifPresent(integer -> this.entityId = integer);
        return buf;
    }

    @Override
    public boolean isCastable() {
        return castedOn() != null && super.isCastable();
    }

    @Environment(EnvType.CLIENT)
    public static class TargetedPlayerSelector {
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
}
