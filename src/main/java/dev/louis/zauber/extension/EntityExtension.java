package dev.louis.zauber.extension;

import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface EntityExtension {
    Optional<PlayerEntity> getTelekineser();
    void setTelekineser(@NotNull PlayerEntity player);
    boolean isTelekinesed();

    void removeTelinesisFrom(PlayerEntity player);
}
