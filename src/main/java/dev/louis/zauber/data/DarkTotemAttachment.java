package dev.louis.zauber.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record DarkTotemAttachment(boolean present) {
    public static final Codec<DarkTotemAttachment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("present").forGetter(DarkTotemAttachment::present)
    ).apply(instance, DarkTotemAttachment::new));

}
