package dev.louis.zauber.extension;

import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface PlayerEntityExtension {
    void zauber$stopTelekinesis();
    void zauber$startTelekinesisOn(@Nullable Entity telekinesisEntity);
    Optional<Entity> zauber$getTelekinesisAffected();
    Optional<Entity> getStaffTargetedEntity();
    Optional<BlockPos> getStaffTargetedBlock();

    void zauber$throwTelekinesis();
}
