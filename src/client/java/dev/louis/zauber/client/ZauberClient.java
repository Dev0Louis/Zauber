package dev.louis.zauber.client;

import dev.louis.nebula.api.spell.SpellType;
import dev.louis.zauber.PlayerViewGetter;
import dev.louis.zauber.Zauber;
import dev.louis.zauber.client.keybind.SpellKeyBinding;
import dev.louis.zauber.client.keybind.SpellKeybindManager;
import dev.louis.zauber.client.render.entity.ManaHorseEntityRenderer;
import dev.louis.zauber.client.render.entity.SpellArrowEntityRenderer;
import dev.louis.zauber.client.screen.SpellTableScreen;
import dev.louis.zauber.client.spell.TargetedPlayerSelector;
import dev.louis.zauber.config.ConfigManager;
import dev.louis.zauber.entity.ManaHorseEntity;
import dev.louis.zauber.entity.SpellArrowEntity;
import dev.louis.zauber.networking.OptionSyncCompletePacket;
import dev.louis.zauber.networking.OptionSyncPacket;
import dev.louis.zauber.particle.ZauberParticleTypes;
import dev.louis.zauber.recipe.ZauberRecipes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.particle.EndRodParticle;
import net.minecraft.client.particle.ExplosionLargeParticle;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Optional;

public class ZauberClient implements ClientModInitializer, PlayerViewGetter {
    private static SpellKeybindManager spellKeybindManager;
    public static ZauberClient INSTANCE;


    @Override
    public void onInitializeClient() {
        INSTANCE = this;
        Zauber.PLAYER_VIEWER_GETTER = this;

        ConfigManager.loadClientConfig();
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            ConfigManager.clearOverrideConfig();
        });

        ClientConfigurationNetworking.registerGlobalReceiver(OptionSyncPacket.TYPE, (packet, responseSender) -> {
            ConfigManager.setOverrideConfig(packet.overrideConfig());
            responseSender.sendPacket(new OptionSyncCompletePacket());
        });
        TargetedPlayerSelector.init();

        createSpellKeyBind(Zauber.Spells.ARROW, false);
        createSpellKeyBind(Zauber.Spells.PULL, false);
        createSpellKeyBind(Zauber.Spells.PUSH, false);
        createSpellKeyBind(Zauber.Spells.REWIND, false);
        createSpellKeyBind(Zauber.Spells.SUICIDE, false);
        createSpellKeyBind(Zauber.Spells.TELEPORT, false);
        createSpellKeyBind(Zauber.Spells.FIRE, false);
        createSpellKeyBind(Zauber.Spells.ICE, false);
        createSpellKeyBind(Zauber.Spells.SUPERNOVA, true);
        createSpellKeyBind(Zauber.Spells.JUGGERNAUT, true);
        createSpellKeyBind(Zauber.Spells.WIND_EXPEL, false);
        createSpellKeyBind(Zauber.Spells.SPROUT, false);
        createSpellKeyBind(Zauber.Spells.DASH, false);
        HandledScreens.register(ZauberRecipes.SPELL_TABLE, SpellTableScreen::new);

        EntityRendererRegistry.register(SpellArrowEntity.TYPE, SpellArrowEntityRenderer::new);
        EntityRendererRegistry.register(ManaHorseEntity.TYPE, ManaHorseEntityRenderer::new);
        ParticleFactoryRegistry.getInstance().register(ZauberParticleTypes.MANA_EXPLOSION, ExplosionLargeParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ZauberParticleTypes.MANA_EXPLOSION_EMITTER, ExplosionLargeParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ZauberParticleTypes.MANA_RUNE, EndRodParticle.Factory::new);
    }

    public static void createSpellKeyBind(SpellType<?> spellType, boolean hides){
        var keybind = KeyBindingHelper.registerKeyBinding(new SpellKeyBinding(spellType, hides));

        getSpellKeybindManager().setSpellKeyBinding(spellType, keybind);
    }

    public static boolean isPlayerTargetable(PlayerEntity targetedPlayer) {
        final var player = MinecraftClient.getInstance().player;
        return player != null && player.canSee(targetedPlayer) && player.isPartOfGame() && !(targetedPlayer.isCreative() || targetedPlayer.isSpectator() || targetedPlayer.isInvisibleTo(targetedPlayer) || player.isSpectator());
    }
    public static SpellKeybindManager getSpellKeybindManager() {
        if (spellKeybindManager != null) return spellKeybindManager;
        return (spellKeybindManager = new SpellKeybindManager());
    }

    public Optional<PlayerEntity> getPlayerInView() {
        return TargetedPlayerSelector.getPlayerInView();
    }
}
