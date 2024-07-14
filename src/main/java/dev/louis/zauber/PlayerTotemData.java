package dev.louis.zauber;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

public record PlayerTotemData<C, E extends Entity>(ActivityChecker<C> activityChecker, EntityType<E> entityType) {
    public interface ActivityChecker<C> {
        boolean isActive(C checked);
    }
}
