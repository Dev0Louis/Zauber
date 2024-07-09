package dev.louis.zauber.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record JuggernautTickComponent(long deletionTick) {
    public static final Codec<JuggernautTickComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.LONG.fieldOf("deletion_tick").forGetter(JuggernautTickComponent::deletionTick)
    ).apply(instance, JuggernautTickComponent::new));
}
