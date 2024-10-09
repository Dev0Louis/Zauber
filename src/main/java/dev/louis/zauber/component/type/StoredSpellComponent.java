package dev.louis.zauber.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.louis.zauber.spell.type.SpellType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryFixedCodec;

public record StoredSpellComponent(RegistryEntry<SpellType<?>> spellType) implements ZauberComponent {
    public static final Codec<RegistryEntry<SpellType<?>>> SPELL_CODE_ENTRY_CODEC = RegistryFixedCodec.of(SpellType.REGISTRY_KEY);
    public static final Codec<StoredSpellComponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(SPELL_CODE_ENTRY_CODEC.fieldOf("spell_type").forGetter(StoredSpellComponent::spellType))
                    .apply(instance, StoredSpellComponent::new)
    );
    public static final PacketCodec<RegistryByteBuf, StoredSpellComponent> PACKET_CODEC = new PacketCodec<>() {
        public StoredSpellComponent decode(RegistryByteBuf registryByteBuf) {
            return new StoredSpellComponent(SpellType.REGISTRY.getEntry(registryByteBuf.readRegistryKey(SpellType.REGISTRY_KEY)).orElseThrow());
        }

        public void encode(RegistryByteBuf registryByteBuf, StoredSpellComponent itemSpellComponent) {
            registryByteBuf.writeRegistryKey(itemSpellComponent.spellType.getKey().orElseThrow());
        }
    };
}
