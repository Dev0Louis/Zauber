package dev.louis.zauber.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.louis.nebula.api.spell.SpellType;
import dev.louis.zauber.component.type.StoredSpellComponent;
import dev.louis.zauber.spell.type.SpellType;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class SpellCastCriterion extends AbstractCriterion<SpellCastCriterion.Conditions> {
    @Override
    public Codec<SpellCastCriterion.Conditions> getConditionsCodec() {
        return SpellCastCriterion.Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, SpellType<?> spellType) {
        this.trigger(player, conditions -> conditions.spellType.value().equals(spellType));
    }

    public static record Conditions(Optional<LootContextPredicate> player,
                                    RegistryEntry<SpellType<?>> spellType) implements AbstractCriterion.Conditions {
        public static final Codec<SpellCastCriterion.Conditions> CODEC = RecordCodecBuilder.create(
                instance ->
                        instance.group(
                                        EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(SpellCastCriterion.Conditions::player),
                                        StoredSpellComponent.SPELL_CODE_ENTRY_CODEC.fieldOf("spell_type").forGetter(SpellCastCriterion.Conditions::spellType)
                                )
                                .apply(instance, SpellCastCriterion.Conditions::new)
        );

        public static AdvancementCriterion<SpellCastCriterion.Conditions> create(RegistryEntry<SpellType<?>> spellType) {
            return ZauberCriteria.SPELL_CAST.create(new SpellCastCriterion.Conditions(Optional.empty(), spellType));
        }
    }
}
