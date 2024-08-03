package dev.louis.zauber;

import dev.louis.nebula.api.spell.SpellType;
import dev.louis.zauber.block.ZauberBlocks;
import dev.louis.zauber.criterion.RitualFinishedCriterion;
import dev.louis.zauber.criterion.SpellCastCriterion;
import dev.louis.zauber.criterion.WakingUpFromNeverEndingSleepCriterion;
import dev.louis.zauber.criterion.ZauberCriteria;
import dev.louis.zauber.entity.ManaArrowEntity;
import dev.louis.zauber.entity.ManaHorseEntity;
import dev.louis.zauber.item.SpellBookItem;
import dev.louis.zauber.item.ZauberItems;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.*;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.DamagePredicate;
import net.minecraft.predicate.TagPredicate;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityEquipmentPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.function.Consumer;

public class ZauberDataGenerator implements DataGeneratorEntrypoint {

    public static FabricDataGenerator generator;
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();
        ZauberDataGenerator.generator = generator;
        pack.addProvider(AdvancementsProvider::new);
    }

    static class AdvancementsProvider extends FabricAdvancementProvider {
        protected AdvancementsProvider(FabricDataOutput dataGenerator) {
            super(dataGenerator, generator.getRegistries());
        }


        @Override
        public void generateAdvancement(RegistryWrapper.WrapperLookup registryLookup, Consumer<AdvancementEntry> consumer) {
            var rootEntry = Advancement.Builder.create()
                    .display(
                            SpellBookItem.createSpellBook(Zauber.Spells.SUPERNOVA),
                            Text.translatable("advancements.zauber.root"),
                            Text.translatable("advancements.zauber.root.description"),
                            Identifier.ofVanilla("textures/gui/advancements/backgrounds/adventure.png"),
                            AdvancementFrame.TASK,
                            true,
                            true,
                            false
                    )
                    .criterion("tick", TickCriterion.Conditions.createTick())
                    .build(consumer, "zauber/root");


            var ritualStone = Advancement.Builder.create()
                    .display(
                            ZauberItems.RITUAL_STONE,
                            Text.translatable("advancements.zauber.ritual_stone"),
                            Text.translatable("advancements.zauber.ritual_stone.description"),
                            null,
                            AdvancementFrame.TASK,
                            true,
                            true,
                            false
                    )
                    .parent(rootEntry)
                    .criterion("ritual_stone", InventoryChangedCriterion.Conditions.items(ZauberItems.RITUAL_STONE))
                    .build(consumer, "zauber/ritual_stone");

            Advancement.Builder.create()
                    .display(
                            ZauberItems.SOUL_HORN,
                            Text.translatable("advancements.zauber.call_goat"),
                            Text.translatable("advancements.zauber.call_goat.description"),
                            null,
                            AdvancementFrame.TASK,
                            true,
                            true,
                            false
                    )
                    .parent(ritualStone)
                    .criterion("soul_horn", SummonedEntityCriterion.Conditions.create(EntityPredicate.Builder.create().type(ManaHorseEntity.TYPE)))
                    .build(consumer, "zauber/call_goat");

            Advancement.Builder.create()
                    .display(
                            Items.COOKED_CHICKEN,
                            Text.translatable("advancements.zauber.cooking_and_smelting"),
                            Text.translatable("advancements.zauber.cooking_and_smelting.description"),
                            null,
                            AdvancementFrame.GOAL,
                            true,
                            true,
                            false
                    )
                    .parent(ritualStone)
                    .criterion("cooking_and_smelting", RitualFinishedCriterion.Conditions.create(Identifier.of("zauber", "smelting")))
                    .build(consumer, "zauber/cooking_and_smelting");

            Advancement.Builder.create()
                    .display(
                            Items.MUD,
                            Text.translatable("advancements.zauber.mudify"),
                            Text.translatable("advancements.zauber.mudify.description"),
                            null,
                            AdvancementFrame.GOAL,
                            true,
                            true,
                            false
                    )
                    .parent(ritualStone)
                    .criterion("mudify", RitualFinishedCriterion.Conditions.create(Identifier.of("zauber", "mudify")))
                    .build(consumer, "zauber/mudify");


            var manaBow = Advancement.Builder.create()
                    .display(
                            ZauberItems.MANA_BOW,
                            Text.translatable("advancements.zauber.mana_bow_creation"),
                            Text.translatable("advancements.zauber.mana_bow_creation.description"),
                            null,
                            AdvancementFrame.GOAL,
                            true,
                            true,
                            false
                    )
                    .parent(ritualStone)
                    .criterion("mana_bow_creation", RitualFinishedCriterion.Conditions.create(Identifier.of("zauber", "mana_bow")))
                    .build(consumer, "zauber/mana_bow_creation");

            Advancement.Builder.create()
                    .display(
                            ZauberItems.MANA_BOW,
                            Text.translatable("advancements.zauber.through_walls"),
                            Text.translatable("advancements.zauber.through_walls.description"),
                            null,
                            AdvancementFrame.GOAL,
                            true,
                            true,
                            false
                    )
                    .parent(manaBow)
                    .criterion("shot_arrow",
                            PlayerHurtEntityCriterion.Conditions.create(
                                    DamagePredicate.Builder.create()
                                            .type(
                                                    DamageSourcePredicate.Builder.create()
                                                            .tag(TagPredicate.expected(DamageTypeTags.IS_PROJECTILE))
                                                            .directEntity(EntityPredicate.Builder.create().type(ManaArrowEntity.TYPE)
                                                            )
                                            )
                            ))
                    .build(consumer, "zauber/through_walls");

            var heartOfIceAdvancement = registerHeartAdvancement("ice", ZauberItems.HEART_OF_THE_ICE, ritualStone, consumer);
            var heartOfDarkness = registerHeartAdvancement("darkness", ZauberItems.HEART_OF_THE_DARKNESS, heartOfIceAdvancement, consumer);
            var darknessInfusion = registerSimple(
                    "infusing_with_darkness",
                    Items.BLACK_BED,
                    "check_inv",
                    Criteria.ANY_BLOCK_USE.create(
                            new AnyBlockUseCriterion.Conditions(
                                    Optional.of(
                                            LootContextPredicate.create(
                                                    EntityPropertiesLootCondition.builder(
                                                            LootContext.EntityTarget.THIS,
                                                            EntityPredicate.Builder.create().equipment(EntityEquipmentPredicate.Builder.create().mainhand(ItemPredicate.Builder.create().items(ZauberItems.HEART_OF_THE_DARKNESS)))).build()
                                            )
                                    ),
                                    Optional.of(
                                            LootContextPredicate.create(
                                                    BlockStatePropertyLootCondition.builder(ZauberBlocks.TRAPPING_BED).build()
                                            )
                                    )
                            )
                    ),
                    heartOfDarkness,
                    AdvancementFrame.TASK,
                    consumer
            );
            registerSimple(
                    "escaping_darkness",
                    Items.BLACK_BED,
                    "waking_up",
                    ZauberCriteria.WAKING_UP_FROM_NEVER_ENDING_SLEEP.create(
                            new WakingUpFromNeverEndingSleepCriterion.Conditions(Optional.empty())
                    ),
                    darknessInfusion,
                    AdvancementFrame.CHALLENGE,
                    consumer
            );

            var totemOfManaAdvancement = registerTotemAdvancement("mana", ZauberItems.TOTEM_OF_MANA, ritualStone, consumer);
            var totemOfIceAdvancement = registerTotemAdvancement("ice", ZauberItems.TOTEM_OF_ICE, totemOfManaAdvancement, consumer);
            registerTotemAdvancement("darkness", ZauberItems.TOTEM_OF_DARKNESS, totemOfIceAdvancement, consumer);

            var spellTableAdvancement = Advancement.Builder.create()
                    .display(
                            ZauberItems.SPELL_TABLE,
                            Text.translatable("advancements.zauber.spell_table"),
                            Text.translatable("advancements.zauber.spell_table.description"),
                            null,
                            AdvancementFrame.TASK,
                            true,
                            true,
                            false
                    )
                    .parent(rootEntry)
                    .criterion("spell_table", InventoryChangedCriterion.Conditions.items(ZauberItems.SPELL_TABLE))
                    .build(consumer, "zauber/spell_table");


            registerSpellTypeAdvancement(Zauber.Spells.REWIND, spellTableAdvancement, AdvancementFrame.CHALLENGE, consumer);
            registerSpellTypeAdvancement(Zauber.Spells.FIRE, spellTableAdvancement, AdvancementFrame.CHALLENGE, consumer);
            var iceSpell = registerSpellTypeAdvancement(Zauber.Spells.ICE, spellTableAdvancement, AdvancementFrame.TASK, consumer);
            registerSpellTypeAdvancement(Zauber.Spells.HAIL_STORM, iceSpell, AdvancementFrame.CHALLENGE, consumer);
        }

        public AdvancementEntry registerSpellTypeAdvancement(SpellType<?> spellType, AdvancementEntry parent, AdvancementFrame frame, Consumer<AdvancementEntry> consumer) {
            return Advancement.Builder.create()
                    .display(
                            SpellBookItem.createSpellBook(spellType),
                            Text.translatable("advancements.zauber.spell_cast_" + spellType.getId().getPath()),
                            Text.translatable("advancements.zauber.spell_cast_" + spellType.getId().getPath()  + ".description"),
                            null,
                            frame,
                            true,
                            false,
                            true
                    )
                    .parent(parent)
                    .criterion("spell_cast_" + spellType.getId().getPath(), SpellCastCriterion.Conditions.create(SpellType.REGISTRY.getEntry(spellType)))
                    .build(consumer, "zauber/spell_cast_" + spellType.getId().getPath());
        }


        public AdvancementEntry registerSimple(String name, Item item, String criterionName, AdvancementCriterion<?> criterion, AdvancementEntry parent, AdvancementFrame advancementFrame, Consumer<AdvancementEntry> consumer) {
            return Advancement.Builder.create()
                    .display(
                            item,
                            Text.translatable("advancements.zauber." + name),
                            Text.translatable("advancements.zauber." + name + ".description"),
                            null,
                            advancementFrame,
                            true,
                            true,
                            false
                    )
                    .parent(parent)
                    .criterion(criterionName, criterion)
                    .build(consumer, "zauber/" + name);
        }


        public AdvancementEntry registerHeartAdvancement(String heart, Item item, AdvancementEntry parent, Consumer<AdvancementEntry> consumer) {
            return Advancement.Builder.create()
                    .display(
                            item,
                            Text.translatable("advancements.zauber.heart_of_the_" + heart),
                            Text.translatable("advancements.zauber.heart_of_the_ " + heart  + ".description"),
                            null,
                            AdvancementFrame.TASK,
                            true,
                            true,
                            false
                    )
                    .parent(parent)
                    .criterion("get_heart_of_the_" + heart, InventoryChangedCriterion.Conditions.items(item))
                    .build(consumer, "zauber/get_heart_of_the_" + heart);
        }
        
        public AdvancementEntry registerTotemAdvancement(String totem, Item item, AdvancementEntry parent, Consumer<AdvancementEntry> consumer) {
            return Advancement.Builder.create()
                    .display(
                            item,
                            Text.translatable("advancements.zauber.totem_of_" + totem),
                            Text.translatable("advancements.zauber.totem_of_" + totem  + ".description"),
                            null,
                            AdvancementFrame.TASK,
                            true,
                            true,
                            false
                    )
                    .parent(parent)
                    .criterion("get_totem_of_" + totem, InventoryChangedCriterion.Conditions.items(item))
                    .build(consumer, "zauber/get_totem_of_" + totem);
        }

    }
}



