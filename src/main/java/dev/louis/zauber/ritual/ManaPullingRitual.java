package dev.louis.zauber.ritual;

public interface ManaPullingRitual {
    default float getCirclingDistance() {
        return 2;
    }
    default float getCirclingSpeed() {
        return 0.01f;
    }

    default boolean shouldPull() {
        return true;
    }
}
