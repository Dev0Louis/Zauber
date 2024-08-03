package dev.louis.zauber.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.StartedRidingCriterion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class RitualFinishedCriterion extends AbstractCriterion<RitualFinishedCriterion.Conditions> {
    @Override
    public Codec<RitualFinishedCriterion.Conditions> getConditionsCodec() {
        return RitualFinishedCriterion.Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, Identifier ritual) {
        this.trigger(player, conditions -> conditions.ritual.equals(ritual));
    }

    public static record Conditions(Optional<LootContextPredicate> player, Identifier ritual) implements AbstractCriterion.Conditions {
        public static final Codec<RitualFinishedCriterion.Conditions> CODEC = RecordCodecBuilder.create(
                instance ->
                        instance.group(
                                EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(RitualFinishedCriterion.Conditions::player),
                                Identifier.CODEC.fieldOf("ritual").forGetter(RitualFinishedCriterion.Conditions::ritual)
                        )
                        .apply(instance, RitualFinishedCriterion.Conditions::new)
        );

        public static AdvancementCriterion<RitualFinishedCriterion.Conditions> create(Identifier id) {
            return ZauberCriteria.RITUAL_FINISHED.create(new RitualFinishedCriterion.Conditions(Optional.empty(), id));
        }
    }
}
