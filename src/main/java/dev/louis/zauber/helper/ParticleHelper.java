package dev.louis.zauber.helper;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3fc;

public class ParticleHelper {
    public static void spawnVelocityParticle(ServerWorld world, Vec3d pos, Vec3d destination, float multiplier, ParticleEffect particleEffect) {
        var veclocity = pos.relativize(destination).normalize();
        world.spawnParticles(
                particleEffect,
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                0,
                veclocity.getX(),
                veclocity.getY(),
                veclocity.getZ(),
                multiplier
        );
    }

    public static void spawnVelocityParticle(ServerWorld world, Position pos, ParticleEffect particleEffect, Vector3fc vector3f) {
        world.spawnParticles(
                particleEffect,
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                0,
                1,
                vector3f.x(),
                vector3f.y(),
                vector3f.z()
        );
    }


    public static void spawnParticles(ServerWorld world, Position pos, ParticleEffect particleEffect, int count, float delta, float speed) {
        world.spawnParticles(
                particleEffect,
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                count,
                delta,
                delta,
                delta,
                speed
        );
    }

    public static void spawn50Particles(ServerWorld world, BlockPos pos, ParticleEffect particleEffect) {
        spawn50Particles(world, pos.toCenterPos(), particleEffect);
    }

    public static void spawnParticle(ServerWorld world, Position pos, float range, int speed, ParticleEffect particleEffect) {
        world.spawnParticles(
                particleEffect,
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                1,
                range,
                range,
                range,
                speed
        );
    }

    public static void spawn50Particles(ServerWorld world, Position pos, ParticleEffect particleEffect) {
        spawnParticles(world, pos, particleEffect, 50, 0.1f, 0.1f);
    }

    //TODO: Make Path Finding a
    public static void spawnConnection(ServerWorld world, Position startPos, Position endPos, ParticleEffect particleEffect, int steps) {
        spawnParticleLine(world, startPos, endPos, particleEffect, steps);
    }

    public static void spawnParticleLine(ServerWorld world, Position startPos, Position endPos, ParticleEffect particleEffect, int steps) {
        for (int i = 0; i < steps; i++) {
            var delta = (double) i / steps;
            //delta = (delta * 0.9) + 0.1;
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
