package dev.louis.zauber.client;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellType;
import dev.louis.zauber.Zauber;
import dev.louis.zauber.client.keybind.SpellKeyBinding;
import dev.louis.zauber.client.keybind.SpellKeybindManager;
import dev.louis.zauber.client.render.entity.ManaHorseEntityRenderer;
import dev.louis.zauber.client.render.entity.SpellArrowEntityRenderer;
import dev.louis.zauber.client.screen.SpellTableScreen;
import dev.louis.zauber.config.ConfigManager;
import dev.louis.zauber.entity.ManaHorseEntity;
import dev.louis.zauber.entity.SpellArrowEntity;
import dev.louis.zauber.networking.OptionSyncCompletePacket;
import dev.louis.zauber.networking.OptionSyncPacket;
import dev.louis.zauber.particle.ZauberParticleTypes;
import dev.louis.zauber.recipe.ZauberRecipes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.particle.DragonBreathParticle;
import net.minecraft.client.particle.ExplosionLargeParticle;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import org.jetbrains.annotations.Nullable;

public class ZauberClient implements ClientModInitializer {
    private static SpellKeybindManager spellKeybindManager;
    public static PlayerEntity playerInView;


    @Override
    public void onInitializeClient() {
        ConfigManager.loadClientConfig();
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            ConfigManager.clearOverrideConfig();
        });

        ClientConfigurationNetworking.registerGlobalReceiver(OptionSyncPacket.TYPE, (packet, responseSender) -> {
            ConfigManager.setOverrideConfig(packet.overrideConfig());
            responseSender.sendPacket(new OptionSyncCompletePacket());
        });

        ClientTickEvents.END_WORLD_TICK.register(world -> {
            var player = world.client.player;
            if (player != null) {
                var result = player.raycast(24, 0, false);
                if (result instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof PlayerEntity playerEntity) {
                    playerInView = playerEntity;
                } else {
                    playerInView = null;
                }
            }
        });

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
        createSpellKeyBind(Zauber.Spells.REFUSAL_OF_DEATH, false);
        createSpellKeyBind(Zauber.Spells.TIME_FREEZE, false);
        createSpellKeyBind(Zauber.Spells.DASH, false);
        HandledScreens.register(ZauberRecipes.SPELL_TABLE, SpellTableScreen::new);

        EntityRendererRegistry.register(SpellArrowEntity.TYPE, SpellArrowEntityRenderer::new);
        EntityRendererRegistry.register(ManaHorseEntity.TYPE, ManaHorseEntityRenderer::new);
        ParticleFactoryRegistry.getInstance().register(ZauberParticleTypes.MANA_EXPLOSION, ExplosionLargeParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ZauberParticleTypes.MANA_EXPLOSION_EMITTER, ExplosionLargeParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ZauberParticleTypes.MANA_RUNE, DragonBreathParticle.Factory::new);
    }

    public static void createSpellKeyBind(SpellType<?> spellType, boolean hides){
        var keybind = KeyBindingHelper.registerKeyBinding(new SpellKeyBinding(spellType, hides));

        getSpellKeybindManager().setSpellKeyBinding(spellType, keybind);
    }

    public static boolean isPlayerTargetable(@Nullable PlayerEntity targetedPlayer) {
        if (targetedPlayer == null || !hasLearnedTargetingSpell()) return false;
        final var player = MinecraftClient.getInstance().player;
        return player != null && player.canSee(targetedPlayer) && player.isPartOfGame() && !(targetedPlayer.isCreative() || targetedPlayer.isSpectator() || targetedPlayer.isInvisibleTo(targetedPlayer) || player.isSpectator());
    }


    private static boolean hasLearnedTargetingSpell() {
        var client = MinecraftClient.getInstance();

        if (client.player == null) return false;
        for (SpellType<? extends Spell> spellType : Zauber.Spells.targetingSpells) {
            if (spellType.isLearnedBy(client.player)) return true;
        }
        return false;
    }

    public static SpellKeybindManager getSpellKeybindManager() {
        if (spellKeybindManager != null) return spellKeybindManager;
        return (spellKeybindManager = new SpellKeybindManager());
    }
}
