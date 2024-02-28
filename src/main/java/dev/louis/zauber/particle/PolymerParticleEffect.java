package dev.louis.zauber.particle;

import eu.pb4.polymer.core.api.utils.PolymerSyncedObject;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PolymerParticleEffect extends ParticleEffect, PolymerSyncedObject<ParticleEffect> {

    @Override
    ParticleEffect getPolymerReplacement(ServerPlayerEntity player);
}
