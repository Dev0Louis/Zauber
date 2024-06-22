package dev.louis.zauber.client;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellType;
import dev.louis.zauber.Zauber;
import dev.louis.zauber.block.ZauberBlocks;
import dev.louis.zauber.client.keybind.SpellKeyBinding;
import dev.louis.zauber.client.keybind.SpellKeybindManager;
import dev.louis.zauber.client.render.entity.BlueArrowEntityRenderer;
import dev.louis.zauber.client.render.entity.ManaHorseEntityRenderer;
import dev.louis.zauber.client.screen.SpellTableScreen;
import dev.louis.zauber.config.ConfigManager;
import dev.louis.zauber.entity.ManaArrowEntity;
import dev.louis.zauber.entity.ManaHorseEntity;
import dev.louis.zauber.entity.SpellArrowEntity;
import dev.louis.zauber.entity.ThrownHeartOfTheIceEntity;
import dev.louis.zauber.item.ZauberItems;
import dev.louis.zauber.networking.OptionSyncCompletePacket;
import dev.louis.zauber.networking.OptionSyncPacket;
import dev.louis.zauber.recipe.ZauberRecipes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
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
        createSpellKeyBind(Zauber.Spells.HAIL_STORM, false);
        createSpellKeyBind(Zauber.Spells.SUPERNOVA, true);
        createSpellKeyBind(Zauber.Spells.JUGGERNAUT, true);
        createSpellKeyBind(Zauber.Spells.WIND_EXPEL, false);
        createSpellKeyBind(Zauber.Spells.SPROUT, false);
        createSpellKeyBind(Zauber.Spells.VENGEANCE, false);
        createSpellKeyBind(Zauber.Spells.DASH, false);
        HandledScreens.register(ZauberRecipes.SPELL_TABLE, SpellTableScreen::new);

        EntityRendererRegistry.register(SpellArrowEntity.TYPE, BlueArrowEntityRenderer::new);
        EntityRendererRegistry.register(ManaArrowEntity.TYPE, BlueArrowEntityRenderer::new);
        EntityRendererRegistry.register(ManaHorseEntity.TYPE, ManaHorseEntityRenderer::new);
        //ParticleFactoryRegistry.getInstance().register(ZauberParticleTypes.MANA_EXPLOSION, ExplosionLargeParticle.Factory::new);
        //ParticleFactoryRegistry.getInstance().register(ZauberParticleTypes.MANA_EXPLOSION_EMITTER, ExplosionLargeParticle.Factory::new);
        //ParticleFactoryRegistry.getInstance().register(ZauberParticleTypes.MANA_RUNE, DragonBreathParticle.Factory::new);

        BlockRenderLayerMap.INSTANCE.putBlock(ZauberBlocks.EXTINGUISHED_TORCH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ZauberBlocks.EXTINGUISHED_WALL_TORCH, RenderLayer.getCutout());
        EntityRendererRegistry.register(ThrownHeartOfTheIceEntity.TYPE, FlyingItemEntityRenderer::new);
        ModelPredicateProviderRegistry.register(ZauberItems.MANA_BOW, new Identifier("pull"), (stack, world, entity, seed) -> {
            if (entity == null) {
                return 0.0F;
            } else {
                return entity.getActiveItem() != stack ? 0.0F : (float)(stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / 20.0F;
            }
        });
        ModelPredicateProviderRegistry.register(
                ZauberItems.MANA_BOW,
                new Identifier("pulling"),
                (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F
        );
        // DEBUG CODE
        /*if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            ClientTickEvents.START_CLIENT_TICK.register(client -> {
                Debugger.check(client.world != null);
            });
            WorldRenderEvents.AFTER_ENTITIES.register(context -> {
                Debugger.getBoxList().forEach(list -> {
                    list.forEach(pair -> {
                        var box1 = pair.getLeft().offset(context.camera().getPos().negate());
                        var color = pair.getRight();
                        DebugRenderer.drawBox(
                                context.matrixStack(),
                                context.consumers(),
                                box1,
                                color.getRed() / 255f,
                                color.getGreen() / 255f,
                                color.getBlue() / 255f,
                                0.7f
                        );
                    });
                });
            });
        }*/
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
