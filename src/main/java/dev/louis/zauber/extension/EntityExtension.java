package dev.louis.zauber.extension;

import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface EntityExtension {
    default Optional<PlayerEntity> zauber$getTelekineser() {
     throw new UnsupportedOperationException("BLOOP BLOOP. I am a Mixin method don't call me >:C");
    }
    default void zauber$setTelekineser(@NotNull PlayerEntity player) {
        throw new UnsupportedOperationException("BLOOP BLOOP. I am a Mixin method don't call me >:C");
    }
    default boolean zauber$isTelekinesed() {
        throw new UnsupportedOperationException("BLOOP BLOOP. I am a Mixin method don't call me >:C");
    }
    default void zauber$removeTelekinesisFrom(PlayerEntity player) {
        throw new UnsupportedOperationException("BLOOP BLOOP. I am a Mixin method don't call me >:C");
    }
}
