package dev.louis.zauber.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

public class WakingUpFromNeverEndingSleepCriterion extends AbstractCriterion<WakingUpFromNeverEndingSleepCriterion.Conditions> {
    @Override
    public Codec<WakingUpFromNeverEndingSleepCriterion.Conditions> getConditionsCodec() {
        return WakingUpFromNeverEndingSleepCriterion.Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player) {
        this.trigger(player, conditions -> true);
    }

    public static record Conditions(Optional<LootContextPredicate> player) implements AbstractCriterion.Conditions {
        public static final Codec<WakingUpFromNeverEndingSleepCriterion.Conditions> CODEC = RecordCodecBuilder.create(
                instance ->
                        instance.group(
                                        EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(WakingUpFromNeverEndingSleepCriterion.Conditions::player)
                                )
                                .apply(instance, WakingUpFromNeverEndingSleepCriterion.Conditions::new)
        );

        public static AdvancementCriterion<WakingUpFromNeverEndingSleepCriterion.Conditions> create() {
            return ZauberCriteria.WAKING_UP_FROM_NEVER_ENDING_SLEEP.create(new WakingUpFromNeverEndingSleepCriterion.Conditions(Optional.empty()));
        }
    }
}
