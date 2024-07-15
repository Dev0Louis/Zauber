package dev.louis.zauber.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.*;
import dev.louis.zauber.Zauber;
import dev.louis.zauber.mana.ManaDirection;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
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
    public static final int VERSION = 4;

    protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    protected static final Path CLIENT_PATH = FabricLoader.getInstance().getConfigDir().resolve("zauber-client.json");
    protected static final Path SERVER_PATH = FabricLoader.getInstance().getConfigDir().resolve("zauber.json");

    //Null in not client env.
    protected static ClientConfig clientConfig;

    //Null in not client env;
    protected static ServerConfig overrideConfig;

    protected static ServerConfig serverConfig;

    public static void loadClientConfig() {
        if(FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
            throw new IllegalStateException("Tried loading client config when not in the client environment");
        }
        ConfigManager.clientConfig = read(ClientConfig.class, CLIENT_PATH);
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

        buf.writeInt(serverConfig.entityTargetingDistance());
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
                buf.readInt()
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

    protected static <T> Collection<? extends ConfigCategory> generateCategories()  {
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
                        colorOption(
                                "entity_targeting_color",
                                Color.RED,
                                () -> getClientConfig().targetingColor(),
                                (value) -> getClientConfig().targetingColor(value)
                        )
                ).build();

        var serverCategory = ConfigCategory.createBuilder()
                .name(category("server"))
                .tooltip(tooltip("server")).option(
                        intSlideOptionServer(
                                "entity_targeting_distance",
                                20,
                                5,
                                128,
                                () -> getServerConfig().entityTargetingDistance(),
                                (value) -> getRealServerConfig().entityTargetingDistance(value)
                        )
                ).option(
                        intSlideOptionServer(
                                "block_targeting_distance",
                                64,
                                5,
                                128,
                                () -> getServerConfig().blockTargetingDistance(),
                                (value) -> getRealServerConfig().blockTargetingDistance(value)
                        )
                ).option(
                        intSlideOptionServer(
                                "spell_cooldown",
                                10,
                                5,
                                100,
                                () -> getServerConfig().spellCooldown(),
                                (value) -> getRealServerConfig().spellCooldown(value)
                        )
                ).option(
                        floatSlideOptionServer(
                                "supernova_explosion_power",
                                16.0f,
                                1.0f,
                                32.0f,
                                () -> getServerConfig().supernovaExplosionPower(),
                                (value) -> getRealServerConfig().supernovaExplosionPower(value)
                        )
                ).option(
                        intFieldOptionServer(
                                "dash_spell_duration",
                                4,
                                () -> getServerConfig().dashSpellDuration(),
                                (value) -> getRealServerConfig().dashSpellDuration(value)
                        )
                ).option(
                        intFieldOptionServer(
                                "sprout_spell_duration",
                                20 * 10,
                                () -> getServerConfig().sproutSpellDuration(),
                                (value) -> getRealServerConfig().sproutSpellDuration(value)
                        )
                ).option(
                        intFieldOptionServer(
                                "fire_spell_duration",
                                20,
                                () -> getServerConfig().fireSpellDuration(),
                                (value) -> getRealServerConfig().fireSpellDuration(value)
                        )
                ).option(
                        intFieldOptionServer(
                                "ice_spell_duration",
                                20,
                                () -> getServerConfig().iceSpellDuration(),
                                (value) -> getRealServerConfig().iceSpellDuration(value)
                        )
                ).option(
                        intFieldOptionServer(
                                "juggernaut_spell_duration",
                                120 * 20,
                                () -> getServerConfig().juggernautSpellDuration(),
                                (value) -> getRealServerConfig().juggernautSpellDuration(value)
                        )
                ).option(
                        intFieldOptionServer(
                                "rewind_spell_duration",
                                6 * 20,
                                () -> getServerConfig().rewindSpellDuration(),
                                (value) -> getRealServerConfig().rewindSpellDuration(value)
                        )
                ).option(
                        doubleSlideOptionServer(
                                "wind_expel_spell_acceleration",
                                0.1,
                                0.01,
                                1,
                                () -> getServerConfig().windExpelSpellAcceleration(),
                                (value) -> getRealServerConfig().windExpelSpellAcceleration(value)
                        )
                ).option(
                        intFieldOptionServer(
                                "wind_expel_spell_duration",
                                20,
                                () -> getServerConfig().windExpelSpellDuration(),
                                (value) -> getRealServerConfig().windExpelSpellDuration(value)
                        )
                )
                .build();
        return List.of(clientCategory, serverCategory);
    }

    protected static Option<Boolean> booleanOption(String optionName, boolean defaultValue, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        return Option.<Boolean>createBuilder()
                .name(option(optionName))
                .description(description(optionName))
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

    protected static Option<Double> doubleSlideOptionServer(String optionName, double defaultValue, double min, double max, Supplier<Double> getter, Consumer<Double> setter) {
        return Option.<Double>createBuilder()
                .name(option(optionName))
                .description(description(optionName))
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

    protected static Option<Float> floatSlideOptionServer(String optionName, float defaultValue, float min, float max, Supplier<Float> getter, Consumer<Float> setter) {
        return Option.<Float>createBuilder()
                .name(option(optionName))
                .description(description(optionName))
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

    protected static Option<Integer> intSlideOptionServer(String optionName, int defaultValue, int min, int max, Supplier<Integer> getter, Consumer<Integer> setter) {
        return Option.<Integer>createBuilder()
                .name(option(optionName))
                .description(description(optionName))
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

    protected static Option<Integer> intFieldOptionServer(String optionName, int defaultValue, Supplier<Integer> getter, Consumer<Integer> setter) {
        return Option.<Integer>createBuilder()
                .name(option(optionName))
                .description(description(optionName))
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

    protected static Option<Color> colorOption(String optionName, Color defaultValue, Supplier<Color> getter, Consumer<Color> setter) {
        return Option.<Color>createBuilder()
                .name(option(optionName))
                //.description(description(optionName))
                .controller(ColorControllerBuilder::create)
                .binding(
                        defaultValue,
                        getter,
                        setter
                )
                .build();
    }

    protected static Option<Integer> intFieldOption(String optionName, int defaultValue, int minValue, Supplier<Integer> getter, Consumer<Integer> setter) {
        return Option.<Integer>createBuilder()
                .name(option(optionName))
                .description(description(optionName))
                .controller(integerOption -> IntegerFieldControllerBuilder.create(integerOption).min(minValue))
                .binding(
                        defaultValue,
                        getter,
                        setter

                )
                .build();
    }

    protected static Option<Integer> intSlideOption(String optionName, int defaultValue, int min, int max, Supplier<Integer> getter, Consumer<Integer> setter) {
        return Option.<Integer>createBuilder()
                .name(option(optionName))
                .description(description(optionName))
                .controller(integerOption -> IntegerSliderControllerBuilder.create(integerOption).range(min, max).step(1))
                .binding(
                        defaultValue,
                        getter,
                        setter
                )
                .build();
    }

    protected static Option<ManaDirection> manaDirectionOption(String optionName, ManaDirection defaultValue, Supplier<ManaDirection> getter, Consumer<ManaDirection> setter) {
        return Option.<ManaDirection>createBuilder()
                .name(option(optionName))
                //.description(description(optionName))
                .controller(manaDirectionOption -> EnumControllerBuilder.create(manaDirectionOption).formatValue(value -> Text.translatable("zauber." + optionName + "." + value.name().toLowerCase())).enumClass(ManaDirection.class))
                .binding(
                        defaultValue,
                        getter,
                        setter
                )
                .build();
    }


    protected static <T> T read(Class<T> configClass, Path path) {
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

    protected static <T> void write(T config, Path path) {
        try {
            Files.writeString(path, GSON.toJson(config));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected static <T> T writeDefault(Class<T> configClass, Path path) throws Exception {
        Files.createDirectories(path.getParent());
        //Files.createFile(path);
        var config = configClass.getDeclaredConstructor().newInstance();
        write(config, path);
        return config;
    }

    protected static MutableText category(String key) {
        return yaclText("category", key);
    }

    protected static MutableText tooltip(String key) {
        return yaclText("tooltip", key);
    }

    protected static MutableText group(String key) {
        return yaclText("group", key);
    }

    protected static OptionDescription description(String key) {
        return OptionDescription.of(Text.translatable("zauber.yacl.option.%s.desc".formatted(key)));
    }

    protected static MutableText option(String key) {
        return yaclText("option", key);
    }

    protected static MutableText yaclText(String category, String key) {
        return Text.translatable("zauber.yacl.%s.%s".formatted(category, key));
    }
}
