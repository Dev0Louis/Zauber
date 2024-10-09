package dev.louis.zauber.client;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.trinkets.api.TrinketsApi;
import dev.louis.nebula.api.spell.Spell;
import dev.louis.zauber.spell.type.SpellType;

import dev.louis.zauber.PlayerTotemData;
import dev.louis.zauber.Zauber;
import dev.louis.zauber.block.ZauberBlocks;
import dev.louis.zauber.client.keybind.SpellKeyBinding;
import dev.louis.zauber.client.keybind.SpellKeybindManager;
import dev.louis.zauber.client.model.SpellUnbakedModel;
import dev.louis.zauber.client.render.entity.BlueArrowEntityRenderer;
import dev.louis.zauber.client.render.entity.ManaHorseEntityRenderer;
import dev.louis.zauber.client.screen.SpellTableScreen;
import dev.louis.zauber.config.ConfigManager;
import dev.louis.zauber.entity.ManaArrowEntity;
import dev.louis.zauber.entity.ManaHorseEntity;
import dev.louis.zauber.entity.SpellArrowEntity;
import dev.louis.zauber.entity.ThrownHeartOfTheIceEntity;
import dev.louis.zauber.item.ZauberItems;
import dev.louis.zauber.networking.OptionSyncCompletePayload;
import dev.louis.zauber.networking.OptionSyncPayload;
import dev.louis.zauber.recipe.ZauberRecipes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ZauberClient implements ClientModInitializer {
    private static SpellKeybindManager spellKeybindManager;
    public static PlayerEntity playerInView;


    @Override
    public void onInitializeClient() {
        ConfigManager.loadClientConfig();
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            ConfigManager.clearOverrideConfig();
        });

        ClientConfigurationNetworking.registerGlobalReceiver(OptionSyncPayload.ID, (packet, context) -> {
            ConfigManager.setOverrideConfig(packet.overrideConfig());
            context.responseSender().sendPacket(new OptionSyncCompletePayload());
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

        HudRenderCallback.EVENT.register((context, tickCounter) -> {
            var client = MinecraftClient.getInstance();
            TrinketsApi.getTrinketComponent(client.player).ifPresent(component -> {
                List<Map.Entry<Item, PlayerTotemData>> sortedList = Zauber.ITEM_TO_TOTEM_DATA.entrySet().stream().collect(Collectors.toList());
                //Collections.shuffle(sortedList);
                sortedList.sort((totemData, totemData2) -> {
                    var active = totemData.getValue().activityChecker().isActive(client.player);
                    var active2 = totemData2.getValue().activityChecker().isActive(client.player);

                    if (active) return -1;
                    if (!active2) return 1;
                    return 1;
                });

                if (!sortedList.isEmpty()) {
                    if (client.currentScreen instanceof AbstractInventoryScreen<?> abstractInventoryScreen && abstractInventoryScreen.hideStatusEffectHud())
                        return;
                    RenderSystem.enableBlend();
                    int i = 0;
                    List<Runnable> list = Lists.newArrayListWithExpectedSize(sortedList.size());

                    for (Map.Entry<Item, PlayerTotemData> entry : sortedList) {
                        if (!component.isEquipped(entry.getKey())) continue;
                        var playerTotemData = entry.getValue();
                        var texture = playerTotemData.texture();
                        int x = 0;
                        int y = 0;

                        x += 24 * i;
                        i++;

                        float totemAlpha = 1.0F;
                        if (!playerTotemData.activityChecker().isActive(MinecraftClient.getInstance().player)) {
                            int age = MinecraftClient.getInstance().player.age;
                            totemAlpha = MathHelper.cos(age / (float) Math.PI * 20.0F) * 0.1f + 0.4f;
                            context.setShaderColor(1.0F, 1.0F, 1.0F, 0.6f);
                        }
                        context.drawGuiTexture(Identifier.of(Zauber.MOD_ID, "artifact/icon_background"), x, y, 22, 22);
                        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

                        int n = x;
                        int o = y;
                        float finalTotemAlpha = totemAlpha;
                        list.add(() -> {
                            context.getMatrices().push();
                            context.setShaderColor(1.0F, 1.0F, 1.0F, finalTotemAlpha);
                            context.getMatrices().translate(0, -0.25, 0);
                            context.drawGuiTexture(texture, n + 3, o + 3, 0, 16, 16);
                            context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                            context.getMatrices().pop();
                        });
                    }

                    list.forEach(Runnable::run);
                    RenderSystem.disableBlend();
                }

            });

            /*AtomicInteger atomicInteger = new AtomicInteger();
            var matrixStack = drawContext.getMatrices();
            matrixStack.scale(4, 4, 4);
            Zauber.ITEM_TO_TOTEM_DATA.forEach((item, playerTotemData) -> {
                matrixStack.push();
                float a = (float) 0.8f;
                int i = atomicInteger.getAndIncrement();

                matrixStack.translate(16 * i, 0, 0);


                drawContext.drawGuiTexture(
                        Identifier.of(Zauber.MOD_ID, "artifact/icon_background"),
                        0,
                        0,
                        16,
                        16
                );

                matrixStack.pop();
                matrixStack.push();

                //matrixStack.translate(16 * i, 0, 0);
                matrixStack.translate(a, a, 0);
                matrixStack.scale(a, a, 0);

                drawContext.drawGuiTexture(
                        playerTotemData.texture(),
                        0,
                        0,
                        16,
                        16
                );

                matrixStack.pop();
            });*/
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
        createSpellKeyBind(Zauber.Spells.CONJOURE_FANG, false);
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
        ModelPredicateProviderRegistry.register(ZauberItems.MANA_BOW, Identifier.ofVanilla("pull"), (stack, world, entity, seed) -> {
            if (entity == null) {
                return 0.0F;
            } else {
                return entity.getActiveItem() != stack ? 0.0F : (float) (stack.getMaxUseTime(entity) - entity.getItemUseTimeLeft()) / 20.0F;
            }
        });
        ModelPredicateProviderRegistry.register(
                ZauberItems.MANA_BOW,
                Identifier.of("pulling"),
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
        ModelLoadingPlugin.register(pluginContext -> {
            pluginContext.resolveModel().register(context -> {
                if (context.id().equals(Identifier.of("zauber:item/spell_book"))) {
                    return new SpellUnbakedModel();
                }
                return null;
            });
            pluginContext.addModels(Zauber.Spells.ZAUBER_SPELLS.stream().map(SpellType::getId).map(identifier -> identifier.withPrefixedPath("item/").withSuffixedPath("_spell_book")).toList());

        });
    }

    public static void createSpellKeyBind(SpellType<?> spellType, boolean hides) {
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
