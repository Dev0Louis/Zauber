package dev.louis.zauber.component;

import dev.louis.zauber.Zauber;
import dev.louis.zauber.component.type.JuggernautTickComponent;
import dev.louis.zauber.component.type.LostBookIdComponent;
import dev.louis.zauber.component.type.StoredSpellComponent;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.UnaryOperator;

public class ZauberDataComponentTypes {
    public static final ComponentType<LostBookIdComponent> LOST_BOOK_CONTENT = register(
            "lost_book_id", builder -> builder.codec(LostBookIdComponent.CODEC).cache()
    );
    public static final ComponentType<StoredSpellComponent> STORED_SPELL_TYPE = register(
            "stored_spell", builder -> builder.codec(StoredSpellComponent.CODEC).packetCodec(StoredSpellComponent.PACKET_CODEC).cache()
    );
    public static final ComponentType<JuggernautTickComponent> JUGGERNAUT_TICK = register(
            "juggernaut_tick", builder -> builder.codec(JuggernautTickComponent.CODEC).cache()
    );

    private static <T> ComponentType<T> register(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        var componentType = Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(Zauber.MOD_ID, id), builderOperator.apply(ComponentType.builder()).build());
        //TODO: FRICKEDY FROGEDY POLYMER BREAKS MY PROPERTY!
        //PolymerComponent.registerDataComponent(componentType);
        return componentType;
    }

    public static void init() {

    }
}
