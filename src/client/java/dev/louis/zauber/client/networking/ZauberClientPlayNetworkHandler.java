package dev.louis.zauber.client.networking;

import dev.louis.zauber.Zauber;
import dev.louis.zauber.extension.PlayerEntityExtension;
import dev.louis.zauber.networking.play.s2c.TelekinesisStatePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;

public class ZauberClientPlayNetworkHandler {

    public static void onTelekinesisState(TelekinesisStatePayload payload, ClientPlayNetworking.Context context) {
        System.out.println("Got: " + payload);

        var entity = context.client().world.getEntityById(payload.playerId());
        if (entity instanceof PlayerEntity player) {
            payload.telekinesedEntityId().ifPresentOrElse(
                    (telekinesedId) -> {
                        context.client().executeSync(() -> {
                            var telekinesed = context.client().world.getEntityById(telekinesedId);
                            if (telekinesed == null) Zauber.LOGGER.error("Couldn't find telekinsed Entity for " + player.getName().getString() + "?");
                            System.out.println(
                                    player.getClass().getSimpleName() + " : " + telekinesed.getName().getString()
                            );
                            ((PlayerEntityExtension) player).zauber$startTelekinesisOn(telekinesed);
                            System.out.println(
                                    "After: " + ((PlayerEntityExtension) player).zauber$getTelekinesisAffected()
                            );
                        });
                    },
                    () -> ((PlayerEntityExtension) player).zauber$startTelekinesisOn(null)
            );

        }
    }
}
