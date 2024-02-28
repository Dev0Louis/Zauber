package dev.louis.zauber;

import net.minecraft.entity.player.PlayerEntity;

import java.util.Optional;

public interface PlayerViewGetter {
    Optional<PlayerEntity> getPlayerInView();
}
