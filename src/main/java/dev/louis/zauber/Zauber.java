package dev.louis.zauber;

import com.mojang.logging.LogUtils;
import dev.louis.nebula.api.event.SpellCastEvent;
import dev.louis.zauber.networking.*;
import dev.louis.zauber.spell.type.SpellType;

import dev.louis.zauber.block.TrappingBedBlock;
import dev.louis.zauber.block.ZauberBlocks;
import dev.louis.zauber.component.item.ZauberDataComponentTypes;
import dev.louis.zauber.component.item.type.LostBookIdComponent;
import dev.louis.zauber.config.ConfigManager;
import dev.louis.zauber.criterion.ZauberCriteria;
import dev.louis.zauber.extension.EntityWithFollowingEntities;
import dev.louis.zauber.entity.*;
import dev.louis.zauber.helper.ParticleHelper;
import dev.louis.zauber.item.*;
import dev.louis.zauber.mana.effect.ZauberPotionEffects;
import dev.louis.zauber.poi.ZauberPointOfInterestTypes;
import dev.louis.zauber.recipe.ZauberRecipes;
import dev.louis.zauber.resource.SpellStructureResourceReloadListener;
import dev.louis.zauber.ritual.Ritual;
import dev.louis.zauber.spell.*;
import dev.louis.zauber.spell.effect.type.SpellEffectTypes;
import dev.louis.zauber.tag.ZauberPotionTags;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetComponentsLootFunction;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("UnreachableCode")
public class Zauber implements ModInitializer {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "zauber";
    public static final Vector3f BLACK_PARTICLE_COLOR = new Vector3f(0, 0, 0);
    private static final ParticleEffect BLACK_PARTICLE = new DustParticleEffect(BLACK_PARTICLE_COLOR, 1);

    @NotNull
    public static final Map<Item, PlayerTotemData> ITEM_TO_TOTEM_DATA;
    public static List<ItemStack> SPELLBOOKS = new ArrayList<>();
    public static List<RegistryEntry<SpellType<?>>> ZAUBER_SPELLS = new ArrayList<>();
    public static List<SpellType<?>> targetingSpells;

    static {
        ITEM_TO_TOTEM_DATA = new HashMap<>();
        ITEM_TO_TOTEM_DATA.put(
                ZauberItems.TOTEM_OF_DARKNESS,
                new PlayerTotemData(
                        TotemOfDarknessItem::isActive,
                        TotemOfDarknessEntity.TYPE,
                        Identifier.of(Zauber.MOD_ID, "artifact/totem_of_darkness")
                )
        );
        ITEM_TO_TOTEM_DATA.put(
                ZauberItems.TOTEM_OF_ICE,
                new PlayerTotemData(
                        TotemOfIceItem::isActive,
                        TotemOfIceEntity.TYPE,
                        Identifier.of(Zauber.MOD_ID, "artifact/totem_of_ice")
                )
        );
        ITEM_TO_TOTEM_DATA.put(
                ZauberItems.TOTEM_OF_MANA,
                new PlayerTotemData(
                        TotemOfManaItem::isActive,
                        TotemOfManaEntity.TYPE,
                        Identifier.of(Zauber.MOD_ID, "artifact/totem_of_mana")
                )
        );
        ITEM_TO_TOTEM_DATA.put(
                Items.TOTEM_OF_UNDYING,
                new PlayerTotemData(
                        (player) -> true,
                        TotemOfUndyingEntity.TYPE,
                        Identifier.of(Zauber.MOD_ID, "artifact/totem_of_undying")
                )
        );
    }


    @Override
    public void onInitialize() {
        ConfigManager.loadServerConfig();

        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            //TODO:REMOVE
            /*ServerTickEvents.START_SERVER_TICK.register(server -> {
                if (server.getPlayerManager().getPlayerList().isEmpty() || server.getOverworld().getTime() % 100 != 0)
                    return;
                ;
                ItemStack itemStack = ZauberItems.LOST_BOOK.getDefaultStack();
                itemStack.set(ZauberDataComponentTypes.LOST_BOOK_CONTENT, new LostBookIdComponent(LostBookType.LOST_BOOKS.get(0).id()));
                server.getPlayerManager().getPlayerList().get(0).getInventory().offerOrDrop(itemStack);

                server.getPlayerManager().getPlayerList().get(0).sendMessage(Text.literal("\u0042")
                        .setStyle(Style.EMPTY.withFont(Identifier.of(Zauber.MOD_ID, "lost_book")))
                        .append(
                                Text.literal("ABCDEFGHI").setStyle(Style.EMPTY.withFont(Style.DEFAULT_FONT_ID))

                        ));
            });*/
        }

        ZauberCriteria.init();
        ZauberPotionTags.init();

        ServerConfigurationConnectionEvents.CONFIGURE.register((handler, server) -> {
            if (ServerConfigurationNetworking.canSend(handler, OptionSyncPayload.ID)) {
                handler.addTask(new OptionSyncTask());
            }
        });

        PayloadTypeRegistry.configurationC2S().register(OptionSyncCompletePayload.ID, OptionSyncCompletePayload.CODEC);
        PayloadTypeRegistry.configurationS2C().register(OptionSyncPayload.ID, OptionSyncPayload.CODEC);
        ServerConfigurationNetworking.registerGlobalReceiver(OptionSyncCompletePayload.ID, (packet, context) -> {
            context.networkHandler().completeTask(OptionSyncTask.KEY);
        });

        PayloadTypeRegistry.playC2S().register(ThrowBlockPayload.ID, ThrowBlockPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(ThrowBlockPayload.ID, ((payload, context) -> {
            var player = context.player();
            var stack = player.getStackInHand(player.getActiveHand());
            var hasStaff = stack.isOf(ZauberItems.STAFF);
            if (hasStaff) {
                ((StaffItem) stack.getItem()).throwBlock(player.getWorld(), player, stack);
            }
        }));

        PayloadTypeRegistry.playS2C().register(TelekinesisPayload.ID, TelekinesisPayload.CODEC);


        Spells.init();
        ZauberRecipes.init();
        ZauberItems.init();
        ZauberBlocks.init();
        ZauberDataComponentTypes.init();
        ZauberPointOfInterestTypes.init();

        SpellCastEvent.AFTER.register((playerEntity, spell) -> {
            if (spell instanceof ZauberSpell<?> zauberSpell) {
                if (playerEntity instanceof ServerPlayerEntity serverPlayer) {
                    ZauberCriteria.SPELL_CAST.trigger(serverPlayer, zauberSpell.getType());
                }
            }
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClient) return ActionResult.PASS;
            var state = world.getBlockState(hitResult.getBlockPos());
            if (player.getStackInHand(hand).isOf(ZauberItems.HEART_OF_THE_DARKNESS) && state.isOf(Blocks.BLACK_BED)) {
                world.setBlockState(hitResult.getBlockPos(), TrappingBedBlock.getStateFor(state), Block.FORCE_STATE);
                var offsetBlockPos = hitResult.getBlockPos().offset(TrappingBedBlock.getDirectionTowardsOtherPart(state.get(BedBlock.PART), state.get(BedBlock.FACING)));
                var otherState = world.getBlockState(offsetBlockPos);
                world.setBlockState(offsetBlockPos, TrappingBedBlock.getStateFor(otherState), Block.FORCE_STATE);

                ParticleHelper.spawnParticles(
                        (ServerWorld) world,
                        hitResult.getBlockPos().toCenterPos(),
                        BLACK_PARTICLE,
                        5,
                        0.5f,
                        0.1f
                );

                ParticleHelper.spawnParticles(
                        (ServerWorld) world,
                        offsetBlockPos.toCenterPos(),
                        BLACK_PARTICLE,
                        5,
                        0.5f,
                        0.1f
                );

                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });

        registerEntity("spell_arrow", SpellArrowEntity.TYPE);
        registerEntity("block_telekinesis", BlockTelekinesisEntity.TYPE);
        registerEntity("haunting_damage", HauntingDamageEntity.TYPE);
        registerEntity("ice_peak", IcePeakEntity.TYPE);
        registerEntity("hail_stone", HailStoneEntity.TYPE);
        registerEntity("totem_of_darkness", TotemOfDarknessEntity.TYPE);
        registerEntity("totem_of_ice", TotemOfIceEntity.TYPE);
        registerEntity("totem_of_mana", TotemOfManaEntity.TYPE);
        registerEntity("totem_of_undying", TotemOfUndyingEntity.TYPE);
        registerEntity("mana_horse", ManaHorseEntity.TYPE);
        registerEntity("thrown_heart_of_the_ice", ThrownHeartOfTheIceEntity.TYPE);
        registerEntity("mana_arrow", ManaArrowEntity.TYPE);
        registerEntity("area_effect_spell", AreaSpellEffectEntity.TYPE);
        registerEntity("hail_storm", HailStormEntity.TYPE);
        FabricDefaultAttributeRegistry.register(ManaHorseEntity.TYPE, ManaHorseEntity.createBaseHorseAttributes());
        //FabricDefaultAttributeRegistry.register(HauntingSword.TYPE, HauntingSword.createBaseAttributes());
        //Registry.register(Registries.PARTICLE_TYPE, new Identifier(MOD_ID, "mana_rune"), ZauberParticleTypes.MANA_RUNE);
        //Registry.register(Registries.PARTICLE_TYPE, new Identifier(MOD_ID, "mana_explosion"), ZauberParticleTypes.MANA_EXPLOSION);
        //Registry.register(Registries.PARTICLE_TYPE, new Identifier(MOD_ID, "mana_explosion_emitter"), ZauberParticleTypes.MANA_EXPLOSION_EMITTER);

        SpellEffectTypes.init();
        Ritual.init();
        ZauberPotionEffects.init();

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SpellStructureResourceReloadListener());

        Registry.register(Registries.ITEM_GROUP, Identifier.of(MOD_ID, "zauber"), FabricItemGroup.builder().icon(() -> SpellBookItem.createSpellBook(SpellType.SUPERNOVA)).displayName(Text.of("Zauber")).entries((displayContext, entries) -> {
            ItemStack itemStack = ZauberItems.SOUL_HORN.getDefaultStack();
            NbtComponent nbtComponent = NbtComponent.DEFAULT.apply(nbt -> nbt.putString("id", "zauber:mana_horse"));

            itemStack.set(DataComponentTypes.ENTITY_DATA, nbtComponent);
            entries.add(itemStack);
            ZauberItems.IN_CREATIVE_INVENTORY.forEach(entries::add);
            SPELLBOOKS.forEach(entries::add);
        }).build());

        LostBookType.init();

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            Identifier gameplayFishingJunkId = Identifier.of("minecraft", "gameplay/fishing/junk");
            if (key.getValue().equals(gameplayFishingJunkId)) {
                tableBuilder.modifyPools(tableBuilder1 -> {
                    ;
                    LostBookType.LOST_BOOKS.forEach(lostBookType -> {
                        tableBuilder1.
                                with(ItemEntry.builder(ZauberItems.LOST_BOOK)
                                        .apply(SetComponentsLootFunction.builder(ZauberDataComponentTypes.LOST_BOOK_CONTENT, new LostBookIdComponent(lostBookType.id())))
                                );
                    });
                });
            }
        });

        AttackEntityCallback.EVENT.register((player, world, hand, target, hitResult) -> {
            if (player.isSpectator() || world.isClient()) return ActionResult.PASS;

            if (hasTotem(player, TotemOfIceEntity.TYPE)) {
                target.setFrozenTicks(Math.min(target.getFrozenTicks() + 50, 400));
            }
            return ActionResult.PASS;
        });
        targetingSpells = List.of(SpellType.PULL, SpellType.PUSH, SpellType.TELEPORT);

    }

    public static boolean hasTotem(LivingEntity livingEntity, EntityType<?> entityType) {
        if (livingEntity instanceof EntityWithFollowingEntities entityWithFollowingEntities) {
            return entityWithFollowingEntities.zauber$getFollowingEntities().stream().anyMatch(entity -> entity.getType().equals(entityType));
        } else {
            return false;
        }
    }

    public static <T extends ParticleEffect> void registerParticle(String path, ParticleType<T> type) {
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Zauber.MOD_ID, path), type);
    }

    public static <T extends Entity> void registerEntity(String path, EntityType<T> type) {
        Registry.register(Registries.ENTITY_TYPE, Identifier.of(Zauber.MOD_ID, path), type);
    }

    public static class Spells {


        public static void init() {
        }
    }

    public static boolean isClientModded(@Nullable ServerPlayerEntity player) {
        return true;
    }

    public static boolean isTrappingBed(World world, BlockPos pos) {
        return world.getBlockState(pos).isOf(ZauberBlocks.TRAPPING_BED);
    }

    public static boolean isInTrappingBed(PlayerEntity player) {
        return player.getSleepingPosition().map(blockPos -> player.getWorld().getBlockState(blockPos).isOf(ZauberBlocks.TRAPPING_BED)).orElse(false);
    }

    public static boolean isNotInTrappingBed(PlayerEntity player) {
        return !isInTrappingBed(player);
    }
}
