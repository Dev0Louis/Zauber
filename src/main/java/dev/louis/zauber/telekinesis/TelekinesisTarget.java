package dev.louis.zauber.telekinesis;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

public sealed interface TelekinesisTarget {
        record BlockTarget(@NotNull BlockPos pos) implements TelekinesisTarget {

        }

        record EntityTarget(int telekinedEntityId) implements TelekinesisTarget {
            public EntityTarget(Entity entity) {
                this(entity.getId());
            }
        }
    }