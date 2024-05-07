package dev.louis.zauber.helper;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Position;

public class ParticleHelper {
    public static void spawnParticle(ServerWorld world, ParticleEffect particleEffect, Position pos) {
        world.spawnParticles(
                particleEffect,
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                100,
                0.1,
                0.1,
                0.1,
                0.1
        );
    }
}
