package dev.louis.zauber.telekinesis;

import net.minecraft.util.math.BlockPos;

public sealed interface TelekinesisTarget {
        record BlockTarget(BlockPos pos) implements TelekinesisTarget {

        }

        record EntityTarget(int telekinedEntityId) implements TelekinesisTarget {

        }
    }