package dev.louis.zauber.client.telekinesis;

import dev.louis.zauber.Zauber;
import dev.louis.zauber.client.render.misc.ZauberRenderLayers;
import dev.louis.zauber.telekinesis.TelekinesisTarget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Predicate;

public class TelekinesisPad {
    public static final Identifier TEXTURE = Identifier.of(Zauber.MOD_ID, "textures/telekinesis_pad.png");


    public TelekinesisTarget target;
    private int targetingTime;


    public Optional<Vec3d> getPos(ClientWorld world) {
        return this.getTargetPos(world).map(targetPos -> world.client.player.getPos().add(world.client.player.getPos().relativize(targetPos).multiply(0.5)));
    }

    public Optional<Vec3d> getTargetPos(World world) {
        return switch (target) {
            case TelekinesisTarget.EntityTarget entityTarget -> {
                var entity = world.getEntityById(entityTarget.telekinedEntityId());
                if (entity == null) yield Optional.empty();
                yield Optional.of(entity.getEyePos());
            }
            case TelekinesisTarget.BlockTarget blockTarget -> Optional.of(blockTarget.pos().toCenterPos());
            case null -> Optional.empty();
        };
    }

    //TODO: Smooth out line position with tickdelta
    public void render(ClientWorld world, Camera camera, MatrixStack stack, VertexConsumerProvider provider) {
        //if (true) return;
        stack.push();
        //stack.translate(1, 2, 1);

        stack.translate(-camera.getPos().x, -camera.getPos().y, -camera.getPos().z);

        /*DebugRenderer.drawBox(
                stack,
                provider,
                box,
                1,
                1,
                0,
                0.1f
        );*/

        var telekinesisPad = provider.getBuffer(ZauberRenderLayers.getTelekinesisPad());
        this.getPos(world).ifPresent(pos -> {
            stack.translate(
                    pos.x,
                    pos.y,
                    pos.z
            );

            stack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(world.client.player.getYaw() % 360));
            stack.translate(-.5, .5, -.5);
            //stack.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(world.client.player.getYaw() % 360));
            //stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(world.client.player.getPitch() % 360));
            telekinesisPad.vertex(stack.peek(), 1, 1, 0).texture(0, 1);
            telekinesisPad.vertex(stack.peek(), 1, 0, 0).texture(0, 0);
            telekinesisPad.vertex(stack.peek(), 0, 0, 0).texture(1, 0);
            telekinesisPad.vertex(stack.peek(), 0, 1, 0).texture(1, 1);
        });



        /*lineConsumer.vertex(stack.peek(), 0.5f, 0.5f, 0)
                .color(255, 255, 255, 255)
                .overlay(0)
                .normal(0, 0, 1);
        lineConsumer.vertex(stack.peek(), (float) lookeyPos.x, (float) lookeyPos.y, 0)
                .color(255, 255, 255, 255)
                .overlay(0)
                .normal(0, 0, 1);*/


        stack.pop();

    }


    public void tick(MinecraftClient client) {
        World world;
        ClientPlayerEntity player;
        if ((world = client.world) == null || (player = client.player) == null) return;

        TelekinesisPad.getTargetedEntity(
                player,
                player.getEntityInteractionRange()
        ).ifPresentOrElse(entity -> {
            var target = new TelekinesisTarget.EntityTarget(entity);
            if (!target.equals(this.target)) this.targetingTime = 0;
            this.target = target;
        }, () -> {
            HitResult hitResult = player.raycast(
                    player.getBlockInteractionRange(),
                    0.0F,
                    false
            );
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                var target = new TelekinesisTarget.BlockTarget(((BlockHitResult) hitResult).getBlockPos());
                if (!target.equals(this.target)) this.targetingTime = 0;
                this.target = target;
            } else {
                target = null;
            }
        });


    }

    public static Optional<Entity> getTargetedEntity(@Nullable Entity entity, double maxDistance) {
        if (entity == null) {
            return Optional.empty();
        } else {
            Vec3d vec3d = entity.getEyePos();
            Vec3d vec3d2 = entity.getRotationVec(1.0F).multiply(maxDistance);
            Vec3d vec3d3 = vec3d.add(vec3d2);
            Box box = entity.getBoundingBox().stretch(vec3d2).expand(1.0);
            double maxDistanceSquared = maxDistance * maxDistance;
            Predicate<Entity> predicate = entityx -> !entityx.isSpectator() && entityx.canHit();
            EntityHitResult entityHitResult = ProjectileUtil.raycast(entity, vec3d, vec3d3, box, predicate, maxDistanceSquared);
            if (entityHitResult == null) {
                return Optional.empty();
            } else {
                return vec3d.squaredDistanceTo(entityHitResult.getPos()) > maxDistanceSquared ? Optional.empty() : Optional.of(entityHitResult.getEntity());
            }
        }
    }
}
