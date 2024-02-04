package dev.louis.zauber.entity;

import dev.louis.zauber.Zauber;
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ZauberEntityType {
    public static final EntityType<SpellArrowEntity> SPELL_ARROW = register(
            "spell_arrow", EntityType.Builder.<SpellArrowEntity>create(SpellArrowEntity::new, SpawnGroup.MISC).setDimensions(0.5F, 0.5F).maxTrackingRange(4).trackingTickInterval(20)
    );

    public static void init() {
        PolymerEntityUtils.registerType(SPELL_ARROW);
        Zauber.LOGGER.info("Entities loaded!");
    }

    private static <T extends Entity> EntityType<T> register(String path, EntityType.Builder<T> type) {
        var id = new Identifier(Zauber.MOD_ID, path);
        return Registry.register(Registries.ENTITY_TYPE, id, type.build(id.toString()));
    }
}
