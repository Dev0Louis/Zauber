package dev.louis.zauber.config;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.*;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.louis.zauber.Zauber;
import dev.louis.zauber.gui.hud.ManaDirection;
import dev.louis.zauber.networking.OptionSyncCompletePacket;
import dev.louis.zauber.networking.OptionSyncPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.nio.file.Path;

public class ZauberConfig {
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("zauber.json5");
    public static ConfigClassHandler<ZauberConfig> HANDLER = ConfigClassHandler.createBuilder(ZauberConfig.class)
            .id(Identifier.of(Zauber.MOD_ID, "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(PATH)
                    .setJson5(true)
                    .build()
            ).build();
    public static boolean HAS_CONFIG_LOADED = false;

    public static void init() {
        HANDLER.load();
        HAS_CONFIG_LOADED = true;
    }

    public static Screen createScreen(@Nullable Screen parent) {
        return HANDLER.generateGui().generateScreen(parent);
    }

    //Client
    public static int syncedTargetingDistance = -1;

    public static int getSyncedTargetingDistance() {
        return syncedTargetingDistance == -1 ? targetingDistance : syncedTargetingDistance;
    }

    @SerialEntry(comment = "Client only", nullable = true)
    @AutoGen(category = "visual", group = "spells")
    @EnumCycler
    public static ManaDirection manaDirection = ManaDirection.RIGHT;

    @SerialEntry(comment = "Client only", nullable = true)
    @AutoGen(category = "visual", group = "spells")
    @IntSlider(min = 1, max = 100, step = 1)
    public static int spellCooldown = 10;

    @SerialEntry(comment = "Client only", nullable = true)
    @AutoGen(category = "visual", group = "spells")
    @IntSlider(min = 1, max = 10, step = 1)
    public static int raycastScanPrecision = 2;

    @SerialEntry(comment = "Client only", nullable = true)
    @AutoGen(category = "visual", group = "spells")
    @ColorField
    public static Color targetingColor = Color.RED;

    public static ManaDirection getManaDirection() {
        return manaDirection;
    }

    public static int getSpellCooldown() {
        return spellCooldown;
    }

    public static int getRaycastScanPrecision() {
        return raycastScanPrecision;
    }

    public static Color getTargetingColor() {
        return targetingColor;
    }

    //Common
    @SerialEntry
    @AutoGen(category = "gameplay", group = "spells")
    @IntSlider(min = 2, max = 128, step = 1)
    private static int targetingDistance = 5;


    @SerialEntry
    @AutoGen(category = "gameplay", group = "spells")
    @FloatField(min = 0, max = 2048)
    private static float supernovaExplosionPower = 16;


    @SerialEntry
    @AutoGen(category = "gameplay", group = "spells.duration")
    @IntField(min = 0)
    private static int dashSpellDuration = 5;

    @SerialEntry
    @AutoGen(category = "gameplay", group = "spells.duration")
    @IntField(min = 0)
    private static int sproutSpellDuration = 20 * 10;

    @SerialEntry
    @AutoGen(category = "gameplay", group = "spells.duration")
    @IntField(min = 0)
    private static int fireSpellDuration = 20;

    @SerialEntry
    @AutoGen(category = "gameplay", group = "spells.duration")
    @IntField(min = 0)
    private static int iceSpellDuration = 20;

    @SerialEntry
    @AutoGen(category = "gameplay", group = "spells.duration")
    @IntField(min = 0)
    private static int juggernautSpellDuration = 120 * 20;

    @SerialEntry
    @AutoGen(category = "gameplay", group = "spells.duration")
    @IntField(min = 0)
    private static int rewindSpellDuration = 6 * 20;

    @SerialEntry
    @AutoGen(category = "gameplay", group = "spells.duration")
    @IntField(min = 0)
    private static int windExpelSpellDuration = 20;

    @SerialEntry
    @AutoGen(category = "gameplay", group = "spells.experimental")
    @TickBox
    private static boolean convertOldNamespace = false;

    public static int getTargetingDistance() {
        return targetingDistance;
    }

    public static float getSupernovaExplosionPower() {
        return supernovaExplosionPower;
    }

    public static int getDashSpellDuration() {
        return dashSpellDuration;
    }

    public static int getSproutSpellDuration() {
        return sproutSpellDuration;
    }

    public static int getIceSpellDuration() {
        return iceSpellDuration;
    }

    public static int getFireSpellDuration() {
        return fireSpellDuration;
    }

    public static int getJuggernautSpellDuration() {
        return juggernautSpellDuration;
    }

    public static int getRewindSpellDuration() {
        return rewindSpellDuration;
    }

    public static int getWindExpelSpellDuration() {
        return windExpelSpellDuration;
    }

    public static boolean shouldConvertOldNamespace() {
        //Is called way to early, so we default to convert.
        if(HANDLER == null)return true;
        return convertOldNamespace;
    }

    public static void syncOptions(OptionSyncPacket optionSyncPacket, PacketSender packetSender) {
        syncedTargetingDistance = optionSyncPacket.targetingDistance();
        packetSender.sendPacket(new OptionSyncCompletePacket());
    }
}
