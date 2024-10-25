package dev.louis.zauber.networking;

import dev.louis.zauber.entity.TelekinedBlockEntity;
import dev.louis.zauber.item.ZauberItems;
import dev.louis.zauber.networking.play.c2s.StartTelekinesisPayload;
import dev.louis.zauber.networking.play.c2s.StopTelekinesisPayload;
import dev.louis.zauber.networking.play.c2s.ThrowTelekinedPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;
import java.util.function.Function;

public class ZauberPlayNetworkHandler {
    public static void onThrowTelekined(ThrowTelekinedPayload ignored, ServerPlayNetworking.Context context) {
        var player = context.player();
        var stack = player.getStackInHand(player.getActiveHand());
        var hasStaff = stack.isOf(ZauberItems.STAFF);
        if (hasStaff) {
            player.zauber$throwTelekined();
        }
    }

    public static void onStartTelekinesis(StartTelekinesisPayload payload, ServerPlayNetworking.Context context) {
        context.server().executeSync(() -> {
            switch (payload.target()) {
                case StartTelekinesisPayload.TelekinesisTarget.BlockTarget(BlockPos pos) -> {
                    var world = context.player().getWorld();
                    var realState = world.getBlockState(pos);
                    if (realState.contains(Properties.DOUBLE_BLOCK_HALF)) return;
                    var state = realState.contains(Properties.WATERLOGGED) ? realState.with(Properties.WATERLOGGED, Boolean.FALSE) : realState;

                    world.setBlockState(pos, realState.getFluidState().getBlockState(), Block.NOTIFY_ALL);

                    TelekinedBlockEntity telekinedBlockEntity = new TelekinedBlockEntity(world, pos.toCenterPos(), state, world.getBlockEntity(pos), context.player());
                    world.spawnEntity(telekinedBlockEntity);
                    context.player().zauber$startTelekinesisOn(telekinedBlockEntity);
                }
                case StartTelekinesisPayload.TelekinesisTarget.EntityTarget(int telekinedEntityId) -> {
                    var entity = context.player().getWorld().getEntityById(telekinedEntityId);
                    context.player().zauber$startTelekinesisOn(entity);
                }
            }
        });
    }

    public static void onStopTelekinesis(StopTelekinesisPayload payload, ServerPlayNetworking.Context context) {
        var player = context.player();
        var stack = player.getStackInHand(player.getActiveHand());
        var hasStaff = stack.isOf(ZauberItems.STAFF);
        if (hasStaff) {
            if (player.isSneaking()) {
                player.zauber$getTelekinesisAffected()
                        .flatMap(cast(TelekinedBlockEntity.class))
                        .ifPresent(TelekinedBlockEntity::placeDirect);
            }
            player.zauber$stopTelekinesis();
        }
    }

    public static <X, T> Function<X, Optional<T>> cast(Class<? extends T> clazz) {
        return x -> {
            if (clazz.isInstance(x)) {
                return Optional.of(clazz.cast(x));
            }
            return Optional.empty();
        };
    }
}
