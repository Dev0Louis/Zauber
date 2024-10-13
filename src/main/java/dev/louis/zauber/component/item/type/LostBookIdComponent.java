package dev.louis.zauber.component.item.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

public record LostBookIdComponent(Identifier id) implements ZauberComponent {
    public static final Codec<LostBookIdComponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(Identifier.CODEC.fieldOf("id").forGetter(LostBookIdComponent::id))
                    .apply(instance, LostBookIdComponent::new)
    );


}
