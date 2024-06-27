package dev.louis.zauber;

import com.mojang.logging.LogUtils;
import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellType;
import dev.louis.zauber.block.TrappingBedBlock;
import dev.louis.zauber.block.ZauberBlocks;
import dev.louis.zauber.config.ConfigManager;
import dev.louis.zauber.entity.*;
import dev.louis.zauber.helper.ParticleHelper;
import dev.louis.zauber.item.SpellBookItem;
import dev.louis.zauber.item.ZauberItems;
import dev.louis.zauber.mana.effect.ZauberPotionEffects;
import dev.louis.zauber.networking.ICanHasZauberPayload;
import dev.louis.zauber.networking.OptionSyncCompletePacket;
import dev.louis.zauber.networking.OptionSyncPacket;
import dev.louis.zauber.networking.OptionSyncTask;
import dev.louis.zauber.poi.ZauberPointOfInterestTypes;
import dev.louis.zauber.recipe.ZauberRecipes;
import dev.louis.zauber.resource.SpellStructureResourceReloadListener;
import dev.louis.zauber.ritual.Ritual;
import dev.louis.zauber.spell.*;
import dev.louis.zauber.tag.ZauberPotionTags;
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import eu.pb4.polymer.networking.api.PolymerNetworking;
import eu.pb4.polymer.networking.api.server.PolymerServerNetworking;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetNbtLootFunction;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnreachableCode")
public class Zauber implements ModInitializer {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "zauber";
    public static final int POLYMER_NETWORK_VERSION = 3;
    public static final Identifier HAS_CLIENT_MODS = Identifier.of(MOD_ID, "has_spell_table");
    public static final Vector3f BLACK_PARTICLE_COLOR = new Vector3f(0, 0, 0);
    private static final ParticleEffect BLACK_PARTICLE = new DustParticleEffect(BLACK_PARTICLE_COLOR, 1);

    private static final ItemStack ITEM_GROUP_LOGO = SpellBookItem.createSpellBook(Spells.SUPERNOVA);

    @Override
    public void onInitialize() {
        ConfigManager.loadServerConfig();
        ZauberPotionTags.init();

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
        ZauberPointOfInterestTypes.init();

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClient) return ActionResult.PASS;
            var state =  world.getBlockState(hitResult.getBlockPos());
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
        registerEntity("haunting_damage", HauntingDamageEntity.TYPE);
        registerEntity("ice_peak", IcePeakEntity.TYPE);
        registerEntity("hail_stone", HailStoneEntity.TYPE);
        //registerEntity("player_following", PlayerFollowingEntity.TYPE);
        registerEntity("mana_horse", ManaHorseEntity.TYPE);
        registerEntity("thrown_heart_of_the_ice", ThrownHeartOfTheIceEntity.TYPE);
        registerEntity("mana_arrow", ManaArrowEntity.TYPE);
        FabricDefaultAttributeRegistry.register(ManaHorseEntity.TYPE, ManaHorseEntity.createBaseHorseAttributes());
        //FabricDefaultAttributeRegistry.register(HauntingSword.TYPE, HauntingSword.createBaseAttributes());
        //Registry.register(Registries.PARTICLE_TYPE, new Identifier(MOD_ID, "mana_rune"), ZauberParticleTypes.MANA_RUNE);
        //Registry.register(Registries.PARTICLE_TYPE, new Identifier(MOD_ID, "mana_explosion"), ZauberParticleTypes.MANA_EXPLOSION);
        //Registry.register(Registries.PARTICLE_TYPE, new Identifier(MOD_ID, "mana_explosion_emitter"), ZauberParticleTypes.MANA_EXPLOSION_EMITTER);

        Ritual.init();
        ZauberPotionEffects.init();

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SpellStructureResourceReloadListener());
        ItemGroup itemGroup = Registry.register(Registries.ITEM_GROUP, Identifier.of(MOD_ID, "zauber"), FabricItemGroup.builder().icon(() -> ITEM_GROUP_LOGO).displayName(Text.of("Zauber")).build());

        ItemGroupEvents.modifyEntriesEvent(Registries.ITEM_GROUP.getKey(itemGroup).get()).register(content -> {
            ItemStack itemStack = ZauberItems.SOUL_HORN.getDefaultStack();
            NbtCompound subNbt = itemStack.getOrCreateSubNbt("stored_entity");
            subNbt.putString("id", Registries.ENTITY_TYPE.getId(ManaHorseEntity.TYPE).toString());
            itemStack.setSubNbt("id", subNbt);
            content.add(itemStack);

            ZauberItems.IN_CREATIVE_INVENTORY.forEach(content::add);
            Spells.SPELLBOOKS.forEach(content::add);
        });
        LostBookType.init();

        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            Identifier gameplayFishingId = Identifier.of("minecraft", "gameplay/fishing");
            if (id.equals(gameplayFishingId)) {
                tableBuilder.modifyPools(tableBuilder1 -> {
                    LostBookType.LOST_BOOKS.forEach(lostBookType -> {
                        NbtCompound nbtCompound = new NbtCompound();
                        nbtCompound.putString("lostBookId", String.valueOf(lostBookType.id()));
                        tableBuilder1.
                                with(ItemEntry.builder(ZauberItems.LOST_BOOK)
                                        .apply(SetNbtLootFunction.builder(nbtCompound))
                                );
                    });
                });
            }
        });
    }

    public static <T extends ParticleEffect> void registerParticle(String path, ParticleType<T> type) {
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Zauber.MOD_ID, path), type);
    }

    public static <T extends Entity> void registerEntity(String path, EntityType<T> type) {
        Registry.register(Registries.ENTITY_TYPE, Identifier.of(Zauber.MOD_ID, path), type);
        PolymerEntityUtils.registerType(type);
    }

    public static class Spells {
        public static List<ItemStack> SPELLBOOKS = new ArrayList<>();
        public static List<SpellType<?>> targetingSpells;

        public static SpellType<ArrowSpell> ARROW = register("arrow", ArrowSpell::new, 2);
        public static SpellType<JuggernautSpell> JUGGERNAUT = register("juggernaut", JuggernautSpell::new, 20);
        public static SpellType<PullSpell> PULL = register("pull", PullSpell::new, 2);
        public static SpellType<PushSpell> PUSH = register("push", PushSpell::new, 2);
        public static SpellType<RewindSpell> REWIND = register("rewind", RewindSpell::new, 3);
        public static SpellType<SuicideSpell> SUICIDE = register("suicide", SuicideSpell::new, 1);
        public static SpellType<TeleportSpell> TELEPORT = register("teleport", TeleportSpell::new, 2);
        public static SpellType<SupernovaSpell> SUPERNOVA = register("supernova", SupernovaSpell::new, 20);
        public static SpellType<FireSpell> FIRE = register("fire", FireSpell::new, 2);
        public static SpellType<IceSpell> ICE = register("ice", IceSpell::new, 2);
        public static SpellType<HailStormSpell> HAIL_STORM = registerParallelCasting("hail_storm", HailStormSpell::new, 3);
        public static SpellType<WindExpelSpell> WIND_EXPEL = register("wind_expel", WindExpelSpell::new, 5);
        public static SpellType<SproutSpell> SPROUT = register("sprout", SproutSpell::new, 2);
        public static SpellType<DashSpell> DASH = register("dash", DashSpell::new, 4);
        public static SpellType<VengeanceSpell> VENGEANCE = register("vengeance", VengeanceSpell::new, 2);


        public static <T extends Spell> SpellType<T> registerNoLearning(String spellName, SpellType.SpellFactory<T> spellFactory, int mana) {
            return SpellType.register(
                    Identifier.of(MOD_ID, spellName),
                    SpellType.Builder.create(spellFactory, mana).needsLearning(false)
            );
        }

        public static <T extends Spell> SpellType<T> registerParallelCasting(String spellName, SpellType.SpellFactory<T> spellFactory, int mana) {
            SpellType<T> spellType = SpellType.register(Identifier.of(MOD_ID, spellName), SpellType.Builder.create(spellFactory, mana).parallelCast());
            SPELLBOOKS.add(SpellBookItem.createSpellBook(spellType));
            return spellType;
        }

        public static <T extends Spell> SpellType<T> register(String spellName, SpellType.SpellFactory<T> spellFactory, int mana) {
            SpellType<T> spellType = SpellType.register(Identifier.of(MOD_ID, spellName), SpellType.Builder.create(spellFactory, mana));
            SPELLBOOKS.add(SpellBookItem.createSpellBook(spellType));
            return spellType;
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

    public static boolean isInTrappingBed(PlayerEntity player) {
        return player.getSleepingPosition().map(blockPos -> player.getWorld().getBlockState(blockPos).isOf(ZauberBlocks.TRAPPING_BED)).orElse(false);
    }

    public static boolean isNotInTrappingBed(PlayerEntity player) {
        return !isInTrappingBed(player);
    }
}
