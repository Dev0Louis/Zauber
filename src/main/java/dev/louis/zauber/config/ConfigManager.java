package dev.louis.zauber.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.*;
import dev.louis.zauber.Zauber;
import dev.louis.zauber.gui.hud.ManaDirection;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ConfigManager {
    public static final int VERSION = 2;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**public static final Codec<ServerConfig> SERVER_CONFIG_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.fieldOf("targeting_distance").forGetter(ServerConfig::targetingDistance),
                    Codec.FLOAT.fieldOf("supernova_explosion_power").forGetter(ServerConfig::supernovaExplosionPower),
                    Codec.INT.fieldOf("dash_spell_duration").forGetter(ServerConfig::dashSpellDuration),
                    Codec.INT.fieldOf("sprout_spell_duration").forGetter(ServerConfig::sproutSpellDuration),
                    Codec.INT.fieldOf("fire_spell_duration").forGetter(ServerConfig::fireSpellDuration),
                    Codec.INT.fieldOf("ice_spell_duration").forGetter(ServerConfig::iceSpellDuration),
                    Codec.INT.fieldOf("juggernaut_spell_duration").forGetter(ServerConfig::juggernautSpellDuration),
                    Codec.INT.fieldOf("rewind_spell_duration").forGetter(ServerConfig::rewindSpellDuration),
                    Codec.INT.fieldOf("wind_expel_spell_duration").forGetter(ServerConfig::windExpelSpellDuration),
                    Codec.BOOL.fieldOf("convert_old_namespace").forGetter(ServerConfig::convertOldNamespace)
            ).apply(instance, ServerConfig::new));**/


    private static final Path CLIENT_PATH = FabricLoader.getInstance().getConfigDir().resolve("zauber-client.json");
    private static final Path SERVER_PATH = FabricLoader.getInstance().getConfigDir().resolve("zauber.json");

    //Null in not client env.
    private static ClientConfig clientConfig;

    //Null in not client env;
    private static ServerConfig overrideConfig;

    private static ServerConfig serverConfig;

    public static void loadClientConfig() {
        if(FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
            throw new IllegalStateException("Tried loading client config when not in the client environment");
        }
        var clientConfig = read(ClientConfig.class, CLIENT_PATH);
        System.out.println(clientConfig);
        ConfigManager.clientConfig = clientConfig;
        Zauber.LOGGER.info("Loaded Client Config");
    }
    public static void loadServerConfig() {
        serverConfig = read(ServerConfig.class, SERVER_PATH);
        Zauber.LOGGER.info("Loaded Server Config");
    }

    public static void saveClientConfig() {
        if(FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
            throw new IllegalStateException("Tried loading client config when not in the client environment");
        }
        write(clientConfig, CLIENT_PATH);
        Zauber.LOGGER.info("Saved Client Config");
    }

    public static void saveServerConfig() {
        write(serverConfig, SERVER_PATH);
        Zauber.LOGGER.info("Saved Server Config");
    }

    public static void clearOverrideConfig() {
        overrideConfig = null;
    }

    public static void writeOverrideConfig(PacketByteBuf buf, ServerConfig serverConfig) {
        buf.writeInt(VERSION);

        buf.writeInt(serverConfig.targetingDistance());
        buf.writeInt(serverConfig.spellCooldown());
        buf.writeFloat(serverConfig.supernovaExplosionPower());
        buf.writeFloat(serverConfig.dashVelocityMultiplier());
        buf.writeInt(serverConfig.dashSpellDuration());
        buf.writeInt(serverConfig.sproutSpellDuration());
        buf.writeInt(serverConfig.fireSpellDuration());
        buf.writeInt(serverConfig.iceSpellDuration());
        buf.writeInt(serverConfig.juggernautSpellDuration());
        buf.writeInt(serverConfig.rewindSpellDuration());
        buf.writeDouble(serverConfig.windExpelSpellAcceleration());
        buf.writeInt(serverConfig.windExpelSpellDuration());
        buf.writeBoolean(serverConfig.convertOldNamespace());
    }

    public static ServerConfig readOverrideConfig(PacketByteBuf buf) {
        var configVersion = buf.readInt();
        if(configVersion != VERSION)
            throw new IllegalStateException("The config version (" + configVersion + ") does not match clients version (" + VERSION + ")!");


        var overrideConfig = new ServerConfig(
                buf.readInt(),
                buf.readInt(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readDouble(),
                buf.readInt(),
                buf.readBoolean()
        );
        Zauber.LOGGER.info("Read Override Config with version " + configVersion);
        return overrideConfig;
    }

    public static void setOverrideConfig(ServerConfig overrideConfig) {
        ConfigManager.overrideConfig = overrideConfig;
    }

    public static ClientConfig getClientConfig() {
        return clientConfig;
    }

    public static ServerConfig getServerConfig() {
        return overrideConfig != null ? overrideConfig : serverConfig;
    }

    public static ServerConfig getRealServerConfig() {
        return serverConfig;
    }

    public static boolean canEditServerConfig() {
        return overrideConfig == null;
    }


    public static Screen createScreen(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(yaclText("title", "main"))
                .categories(generateCategories())
                .save(() -> {
                    write(serverConfig, SERVER_PATH);
                    if(clientConfig != null) {
                        write(clientConfig, CLIENT_PATH);
                    }
                })
                .build()
                .generateScreen(parent);
    }

    private static <T> Collection<? extends ConfigCategory> generateCategories() {
        var clientCategory = ConfigCategory.createBuilder()
                .name(category("client"))
                .tooltip(tooltip("client"))
                .option(
                        manaDirectionOption(
                                "mana_direction",
                                ManaDirection.LEFT,
                                () -> getClientConfig().manaDirection(),
                                (value) -> getClientConfig().manaDirection(value)
                        )
                )
                .option(
                        intSlideOption(
                                "raycast_scan_precision",
                                2,
                                1,
                                10,
                                () -> getClientConfig().raycastScanPrecision(),
                                (value) -> getClientConfig().raycastScanPrecision(value)
                        )
                )
                .option(
                        colorOption(
                                "targeting_color",
                                Color.RED,
                                () -> getClientConfig().targetingColor(),
                                (value) -> getClientConfig().targetingColor(value)
                        )
                ).build();

                        var serverCategory = ConfigCategory.createBuilder()
                .name(category("server"))
                .tooltip(tooltip("server"))
                .option(
                        intSlideOptionServer(
                                "targeting_distance",
                                20,
                                5,
                                128,
                                () -> getServerConfig().targetingDistance(),
                                (value) -> getRealServerConfig().targetingDistance(value)
                        )
                )
                .option(
                        intSlideOptionServer(
                                "spell_cooldown",
                                10,
                                5,
                                100,
                                () -> getServerConfig().spellCooldown(),
                                (value) -> getServerConfig().spellCooldown(value)
                        )
                )
                .option(
                        floatSlideOptionServer(
                                "supernova_explosion_power",
                                16.0f,
                                1.0f,
                                32.0f,
                                () -> getServerConfig().supernovaExplosionPower(),
                                (value) -> getRealServerConfig().supernovaExplosionPower(value)
                        )
                )
                .option(
                        intFieldOptionServer(
                                "dash_spell_duration",
                                4,
                                () -> getServerConfig().dashSpellDuration(),
                                (value) -> getRealServerConfig().dashSpellDuration(value)
                        )
                )
                .option(
                        intFieldOptionServer(
                                "sprout_spell_duration",
                                20 * 10,
                                () -> getServerConfig().sproutSpellDuration(),
                                (value) -> getRealServerConfig().sproutSpellDuration(value)
                        )
                )
                .option(
                        intFieldOptionServer(
                                "fire_spell_duration",
                                20,
                                () -> getServerConfig().fireSpellDuration(),
                                (value) -> getRealServerConfig().fireSpellDuration(value)
                        )
                )
                .option(
                        intFieldOptionServer(
                                "ice_spell_duration",
                                20,
                                () -> getServerConfig().iceSpellDuration(),
                                (value) -> getRealServerConfig().iceSpellDuration(value)
                        )
                )
                .option(
                        intFieldOptionServer(
                                "juggernaut_spell_duration",
                                120 * 20,
                                () -> getServerConfig().juggernautSpellDuration(),
                                (value) -> getRealServerConfig().juggernautSpellDuration(value)
                        )
                )
                .option(
                        intFieldOptionServer(
                                "rewind_spell_duration",
                                6 * 20,
                                () -> getServerConfig().rewindSpellDuration(),
                                (value) -> getRealServerConfig().rewindSpellDuration(value)
                        )
                )
                .option(
                        doubleSlideOptionServer(
                                "wind_expel_spell_acceleration",
                                0.1,
                                0.01,
                                1,
                                () -> getServerConfig().windExpelSpellAcceleration(),
                                (value) -> getRealServerConfig().windExpelSpellAcceleration(value)
                        )
                )
                .option(
                        intFieldOptionServer(
                                "wind_expel_spell_duration",
                                20,
                                () -> getServerConfig().windExpelSpellDuration(),
                                (value) -> getRealServerConfig().windExpelSpellDuration(value)
                        )
                )
                .option(
                        booleanOption(
                                "convert_old_namespace",
                                false,
                                () -> getServerConfig().convertOldNamespace(),
                                (value) -> getRealServerConfig().convertOldNamespace(value)
                        )
                ).build();
        return List.of(clientCategory, serverCategory);
    }

    private static Option<Boolean> booleanOption(String optionName, boolean defaultValue, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        return Option.<Boolean>createBuilder()
                .name(option(optionName))
                .controller(TickBoxControllerBuilder::create)
                .binding(
                        getter.get(),
                        getter,
                        (value) -> {
                            if (canEditServerConfig()) setter.accept(value);
                        }
                )
                .available(canEditServerConfig())
                .build();
    }

    private static Option<Double> doubleSlideOptionServer(String optionName, double defaultValue, double min, double max, Supplier<Double> getter, Consumer<Double> setter) {
        return Option.<Double>createBuilder()
                .name(option(optionName))
                .controller(integerOption -> DoubleSliderControllerBuilder.create(integerOption).range(min, max).step(0.01))
                .binding(
                        defaultValue,
                        getter,
                        (integer) -> {
                            if (canEditServerConfig()) setter.accept(integer);
                        }
                )
                .available(canEditServerConfig())
                .build();
    }

    private static Option<Float> floatSlideOptionServer(String optionName, float defaultValue, float min, float max, Supplier<Float> getter, Consumer<Float> setter) {
        return Option.<Float>createBuilder()
                .name(option(optionName))
                .controller(integerOption -> FloatSliderControllerBuilder.create(integerOption).range(min, max).step(0.1f))
                .binding(
                        defaultValue,
                        getter,
                        (integer) -> {
                            if (canEditServerConfig()) setter.accept(integer);
                        }
                )
                .available(canEditServerConfig())
                .build();
    }

    private static Option<Integer> intSlideOptionServer(String optionName, int defaultValue, int min, int max, Supplier<Integer> getter, Consumer<Integer> setter) {
        return Option.<Integer>createBuilder()
                .name(option(optionName))
                .controller(integerOption -> IntegerSliderControllerBuilder.create(integerOption).range(min, max).step(1))
                .binding(
                        defaultValue,
                        getter,
                        (integer) -> {
                            if (canEditServerConfig()) setter.accept(integer);
                        }
                )
                .available(canEditServerConfig())
                .build();
    }

    private static Option<Integer> intFieldOptionServer(String optionName, int defaultValue, Supplier<Integer> getter, Consumer<Integer> setter) {
        return Option.<Integer>createBuilder()
                .name(option(optionName))
                .controller(integerOption -> IntegerFieldControllerBuilder.create(integerOption).min(0))
                .binding(
                        defaultValue,
                        getter,
                        (integer) -> {
                            if (canEditServerConfig()) setter.accept(integer);
                        }
                )
                .available(canEditServerConfig())
                .build();
    }

    private static Option<Color> colorOption(String optionName, Color defaultValue, Supplier<Color> getter, Consumer<Color> setter) {
        return Option.<Color>createBuilder()
                .name(option(optionName))
                .controller(ColorControllerBuilder::create)
                .binding(
                        defaultValue,
                        getter,
                        setter
                )
                .build();
    }

    private static Option<Integer> intFieldOption(String optionName, int defaultValue, int minValue, Supplier<Integer> getter, Consumer<Integer> setter) {
        return Option.<Integer>createBuilder()
                .name(option(optionName))
                .controller(integerOption -> IntegerFieldControllerBuilder.create(integerOption).min(minValue))
                .binding(
                        defaultValue,
                        getter,
                        setter

                )
                .build();
    }

    private static Option<Integer> intSlideOption(String optionName, int defaultValue, int min, int max, Supplier<Integer> getter, Consumer<Integer> setter) {
        return Option.<Integer>createBuilder()
                .name(option(optionName))
                .controller(integerOption -> IntegerSliderControllerBuilder.create(integerOption).range(min, max).step(1))
                .binding(
                        defaultValue,
                        getter,
                        setter
                )
                .build();
    }

    private static Option<ManaDirection> manaDirectionOption(String optionName, ManaDirection defaultValue, Supplier<ManaDirection> getter, Consumer<ManaDirection> setter) {
        return Option.<ManaDirection>createBuilder()
                .name(option(optionName))
                .controller(manaDirectionOption -> EnumControllerBuilder.create(manaDirectionOption).enumClass(ManaDirection.class))
                .binding(
                        defaultValue,
                        getter,
                        setter
                )
                .build();
    }


    private static <T> T read(Class<T> configClass, Path path) {
        try {
            if (!Files.exists(path)) {
                writeDefault(configClass, path);
            }
            var returnValue = GSON.fromJson(Files.readString(path), configClass);
            if (returnValue == null) return writeDefault(configClass, path);
            return returnValue;
        } catch (Exception e) {
            try {
                Zauber.LOGGER.error("Couldn't load the config using default.");
                return writeDefault(configClass, path);
            } catch (Exception ex) {
                throw new RuntimeException("Couldn't load the default config something is wrong?", e);
            }
        }
    }

    private static <T> void write(T config, Path path) {
        try {
            Files.writeString(path, GSON.toJson(config));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T writeDefault(Class<T> configClass, Path path) throws Exception {
        Files.createDirectories(path.getParent());
        //Files.createFile(path);
        var config = configClass.getDeclaredConstructor().newInstance();
        write(config, path);
        return config;
    }

    private static MutableText category(String key) {
        return yaclText("category", key);
    }

    private static MutableText tooltip(String key) {
        return yaclText("tooltip", key);
    }

    private static MutableText group(String key) {
        return yaclText("group", key);
    }

    private static MutableText description(String key) {
        return yaclText("description", key);
    }

    private static MutableText option(String key) {
        return yaclText("option", key);
    }

    private static MutableText yaclText(String category, String key) {
        return Text.translatable("zauber.yacl.%s.%s".formatted(category, key));
    }
}
