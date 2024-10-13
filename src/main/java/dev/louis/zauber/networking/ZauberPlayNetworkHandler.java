package dev.louis.zauber.networking;

import dev.louis.zauber.entity.BlockTelekinesisEntity;
import dev.louis.zauber.extension.PlayerEntityExtension;
import dev.louis.zauber.item.StaffItem;
import dev.louis.zauber.item.ZauberItems;
import dev.louis.zauber.networking.play.c2s.StartTelekinesisPayload;
import dev.louis.zauber.networking.play.c2s.StopTelekinesisPayload;
import dev.louis.zauber.networking.play.c2s.ThrowBlockPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.state.property.Properties;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;

public class ZauberPlayNetworkHandler {
    public static void onThrowBlock(ThrowBlockPayload ignored, ServerPlayNetworking.Context context) {
        var player = context.player();
        var stack = player.getStackInHand(player.getActiveHand());
        var hasStaff = stack.isOf(ZauberItems.STAFF);
        if (hasStaff) {
            ((StaffItem) stack.getItem()).throwBlock(player.getWorld(), player, stack);
        }
    }

    public static void onStartTelekinesis(StartTelekinesisPayload payload, ServerPlayNetworking.Context context) {
        context.server().executeSync(() -> {
            switch (payload.target()) {
                case StartTelekinesisPayload.TelekinesisTarget.BlockTarget(BlockPos pos) -> {
                    var world = context.player().getWorld();
                    var realState = world.getBlockState(pos);
                    var state = realState.contains(Properties.WATERLOGGED) ? realState.with(Properties.WATERLOGGED, Boolean.FALSE) : realState;

                    world.setBlockState(pos, realState.getFluidState().getBlockState(), Block.NOTIFY_ALL);

                    BlockTelekinesisEntity blockTelekinesisEntity = new BlockTelekinesisEntity(world, pos.toCenterPos(), state, world.getBlockEntity(pos), context.player());
                    world.spawnEntity(blockTelekinesisEntity);
                    ((PlayerEntityExtension) context.player()).zauber$startTelekinesisOn(blockTelekinesisEntity);
                }
                case StartTelekinesisPayload.TelekinesisTarget.EntityTarget(int telekinedEntityId) -> {
                    var entity = context.player().getWorld().getEntityById(telekinedEntityId);
                    ((PlayerEntityExtension) context.player()).zauber$startTelekinesisOn(entity);
                }
            }
        });
    }

    public static void onStopTelekinesis(StopTelekinesisPayload payload, ServerPlayNetworking.Context context) {

    }
}
