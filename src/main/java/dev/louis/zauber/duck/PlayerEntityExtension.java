package dev.louis.zauber.duck;

import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface PlayerEntityExtension {
    void zauber$stopTelekinesis();
    void zauber$startTelekinesisOn(@Nullable Entity telekinesisEntity);
    Optional<Entity> zauber$getTelekinesisAffected();
    Optional<LivingEntity> getStaffTargetedEntity();
    Optional<CachedBlockPosition> getStaffTargetedBlock();

    void zauber$throwTelekinesis();
}
