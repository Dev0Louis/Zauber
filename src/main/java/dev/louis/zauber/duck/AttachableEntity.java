package dev.louis.zauber.duck;

import dev.louis.zauber.entity.TotemOfDarknessEntity;

public interface AttachableEntity {
    default TotemOfDarknessEntity zauber$getTotemOfDarkness() {
        throw new IllegalStateException("Mixin Beep Boop Error");
    }
}
