package dev.louis.zauber.criterion;

import eu.pb4.polymer.rsm.api.RegistrySyncUtils;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ZauberCriteria {
    public static final RitualFinishedCriterion RITUAL_FINISHED = register("ritual_finished_criterion", new RitualFinishedCriterion());
    public static final SpellCastCriterion SPELL_CAST = register("spell_cast", new SpellCastCriterion());
    public static final WakingUpFromNeverEndingSleepCriterion WAKING_UP_FROM_NEVER_ENDING_SLEEP = register("waking_up_from_never_ending_sleep", new WakingUpFromNeverEndingSleepCriterion());

    public static <T extends Criterion<?>> T register(String id, T criterion) {
        T a = Registry.register(Registries.CRITERION, id, criterion);
        RegistrySyncUtils.setServerEntry(Registries.CRITERION, a);
        return a;
    }

    public static void init() {

    }
}
