package dev.louis.zauber.client.telekinesis;

import dev.louis.zauber.Zauber;
import dev.louis.zauber.client.render.misc.ZauberRenderLayers;
import dev.louis.zauber.networking.play.c2s.StartTelekinesisPayload;
import dev.louis.zauber.telekinesis.TelekinesisTarget;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;

import java.util.Optional;

public class TelekinesisPad {
    public static final Identifier TEXTURE = Identifier.of(Zauber.MOD_ID, "textures/telekinesis_pad.png");
    float FINENESS = 0.01f;


    private final BlockPos blockPos;
    private final Vec3d pos;
    private final TelekinesisTarget telekinesisTarget;
    private final Box box;

    private Vector2f lastPos;

    public TelekinesisPad(BlockPos blockPos, Vec3d pos1, TelekinesisTarget telekinesisTarget) {
        this.blockPos = blockPos;
        this.pos = blockPos.toCenterPos();
        this.box = Box.of(pos, 1, 1, 0.05);
        this.telekinesisTarget = telekinesisTarget;
    }


    public Vec3d getPos() {
        return pos;
    }

    //TODO: Smooth out line position with tickdelta
    public void render(ClientWorld world, Camera camera, MatrixStack stack, VertexConsumerProvider provider) {
        //if (true) return;
        stack.push();
        //stack.translate(1, 2, 1);

        stack.translate(-camera.getPos().x, -camera.getPos().y, -camera.getPos().z);

        Optional<Vector2f> maybeIntersectionPos = findMaybeInterestion(world, FINENESS);


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
        stack.translate(pos.x, pos.y, pos.z - 0.001);
        telekinesisPad.vertex(stack.peek(), 1, 1, 0).texture(0, 0);
        telekinesisPad.vertex(stack.peek(), 1, 0, 0).texture(0, 1);
        telekinesisPad.vertex(stack.peek(), 0, 0, 0).texture(1, 1);
        telekinesisPad.vertex(stack.peek(), 0, 1, 0).texture(1, 0);



        maybeIntersectionPos.ifPresent(intersectionPos -> {
            var lineConsumer = provider.getBuffer(ZauberRenderLayers.LINES);

            lineConsumer.vertex(stack.peek(), 0.5f, 0.5f, 0)
                    .color(255, 255, 255, 255)
                    .overlay(0)
                    .normal(0, 0, 1);
            lineConsumer.vertex(stack.peek(), (float) intersectionPos.x, (float) intersectionPos.y, 0)
                    .color(255, 255, 255, 255)
                    .overlay(0)
                    .normal(0, 0, 1);

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

    private @NotNull Optional<Vector2f> findMaybeInterestion(ClientWorld world, float fineness) {
        Optional<Vector2f> maybeIntersectionPos = Optional.empty();

        for (int i = 0; i < 5 * (1 / fineness); i++) {
            var player = world.client.player;
            var rotVec = player.getRotationVec(1).multiply(i * fineness);
            var newPos = player.getEyePos().add(rotVec);
            Box testBox = Box.of(newPos, 0.01, 0.01, 0.01);

            var active = testBox.intersects(box);

            /*DebugRenderer.drawBox(
                    stack,
                    provider,
                    testBox,
                    active ? 0 : 1,
                    active ? 1 : 0,
                    0,
                    0.1f
            );*/
            if (active) {
                maybeIntersectionPos = Optional.of(new Vector2f((float) (newPos.x - pos.x), (float) (newPos.y - pos.y)));
                break;
            }
        }
        return maybeIntersectionPos;
    }

    public void onLeavePad() {
        if (lastPos.y > .75) {
            ClientPlayNetworking.send(new StartTelekinesisPayload(blockPos));
        }
    }

    Vec3d getPlayerLookyPos(PlayerEntity player) {
        Vec3d vec3d = player.getRotationVec(1.0F).normalize();
        Vec3d vec3d2 = new Vec3d(this.getPos().getX() - player.getX(), this.getPos().getY() - player.getEyeY(), this.getPos().getZ() - player.getZ());
        double d = vec3d2.length();
        vec3d2 = vec3d2.normalize();
        double e = vec3d.dotProduct(vec3d2);
        //return e > 1.0 - 0.025 / d ? true : false;
        return vec3d2;
    }

    public void tick(MinecraftClient client) {
        var newLastPos = findMaybeInterestion(client.world, FINENESS);


        if (newLastPos.isPresent()) {
            this.lastPos = newLastPos.get();
        } else {
            if (this.lastPos != null) onLeavePad();
            this.lastPos = null;
        }
    }
}
