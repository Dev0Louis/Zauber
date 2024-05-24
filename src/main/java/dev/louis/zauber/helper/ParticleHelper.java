package dev.louis.zauber.helper;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;

public class ParticleHelper {
    public static void spawnParticle(ServerWorld world, Position pos, ParticleEffect particleEffect) {
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

    public static void spawnParticleLine(ServerWorld world, Position startPos, Position endPos, ParticleEffect particleEffect, int steps) {
        for (int i = 0; i < steps; i++) {
            var delta = (double) i / steps;
            delta = (delta * 0.9) + 0.1;
            var x = MathHelper.lerp(delta, startPos.getX(), endPos.getX());
            var y = MathHelper.lerp(delta, startPos.getY(), endPos.getY());
            var z = MathHelper.lerp(delta, startPos.getZ(), endPos.getZ());
            Vec3d particlePos = new Vec3d(x, y, z);

            world.spawnParticles(
                    particleEffect,
                    particlePos.getX(),
                    particlePos.getY(),
                    particlePos.getZ(),
                    1,
                    0,
                    0,
                    0,
                    0.0
            );
        }
    }
}
