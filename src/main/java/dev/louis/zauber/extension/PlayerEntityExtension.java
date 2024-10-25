package dev.louis.zauber.extension;

import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface PlayerEntityExtension {
    default void zauber$stopTelekinesis() {
     throw new UnsupportedOperationException("BLOOP BLOOP. I am a Mixin method don't call me >:C");
    }
    default void zauber$startTelekinesisOn(@Nullable Entity telekinesisEntity) {
     throw new UnsupportedOperationException("BLOOP BLOOP. I am a Mixin method don't call me >:C");
    }
    default Optional<Entity> zauber$getTelekinesisAffected() {
     throw new UnsupportedOperationException("BLOOP BLOOP. I am a Mixin method don't call me >:C");
    }
    default void zauber$throwTelekined() {
     throw new UnsupportedOperationException("BLOOP BLOOP. I am a Mixin method don't call me >:C");
    }
}
