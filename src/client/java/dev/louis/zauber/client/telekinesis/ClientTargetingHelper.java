package dev.louis.zauber.client.telekinesis;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.Optional;
import java.util.function.Predicate;

public class ClientTargetingHelper {
    private static final int TARGETING_DISTANCE = 32;

    public static Optional<Entity> getTargetedEntity() {
        final int maxDistance = TARGETING_DISTANCE;
        final var tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true);

        var player = MinecraftClient.getInstance().player;
        if (player != null) {
            Vec3d eyePos = player.getEyePos();
            Vec3d rotation = player.getRotationVec(tickDelta).multiply(maxDistance);
            Vec3d start = eyePos.add(rotation);
            Box box = player.getBoundingBox().stretch(rotation).expand(1.0);
            int maxDistanceSquared = maxDistance * maxDistance;
            Predicate<Entity> predicate = entityx -> !entityx.isSpectator() && entityx.canHit() && entityx.getVehicle() == null;
            EntityHitResult entityHitResult = ProjectileUtil.raycast(player, eyePos, start, box, predicate, maxDistanceSquared);
            if (entityHitResult != null) {
                return eyePos.squaredDistanceTo(entityHitResult.getPos()) > (double) maxDistanceSquared ? Optional.empty() : Optional.of(entityHitResult.getEntity());
            }
        }
        return Optional.empty();
    }

    public static Optional<BlockPos> getTargetBlock() {
        var player = MinecraftClient.getInstance().player;
        var tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true);
        if (player != null) {
            var rayCast = raycastBlock(player, tickDelta);
            if (
                    rayCast.getType() == HitResult.Type.BLOCK &&
                            !player.getWorld().getBlockState(((BlockHitResult) rayCast).getBlockPos())
                                    .contains(Properties.DOUBLE_BLOCK_HALF)
            ) {
                return Optional.of(((BlockHitResult) rayCast).getBlockPos());
            }
        }
        return Optional.empty();
    }

    private static HitResult raycastBlock(PlayerEntity player, float tickDelta) {
        final double maxDistance = TARGETING_DISTANCE;

        Vec3d vec3d = player.getCameraPosVec(tickDelta);
        Vec3d vec3d2 = player.getRotationVec(tickDelta);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * maxDistance, vec3d2.y * maxDistance, vec3d2.z * maxDistance);
        return player.getWorld().raycast(new RaycastContext(vec3d, vec3d3, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player));
    }
}
