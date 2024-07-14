package dev.louis.zauber;

import dev.louis.zauber.entity.FollowingEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public record PlayerTotemData(ActivityChecker activityChecker, EntityType<? extends FollowingEntity> entityType, Identifier texture) {
    public interface ActivityChecker {
        boolean isActive(PlayerEntity checked);
    }
}
