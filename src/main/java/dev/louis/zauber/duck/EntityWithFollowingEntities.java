package dev.louis.zauber.duck;

import dev.louis.zauber.entity.FollowingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface EntityWithFollowingEntities {
    default @NotNull List<FollowingEntity> zauber$getFollowingEntities() {
        throw new IllegalStateException("Mixin Beep Boop Error");
    }
}
