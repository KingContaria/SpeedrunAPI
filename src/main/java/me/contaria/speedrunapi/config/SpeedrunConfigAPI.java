package me.contaria.speedrunapi.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import me.contaria.speedrunapi.config.api.SpeedrunConfig;
import me.contaria.speedrunapi.config.api.SpeedrunConfigScreenProvider;
import me.contaria.speedrunapi.config.api.SpeedrunConfigStorage;
import me.contaria.speedrunapi.config.api.SpeedrunOption;
import me.contaria.speedrunapi.config.api.annotations.Config;
import me.contaria.speedrunapi.config.exceptions.InitializeConfigException;
import me.contaria.speedrunapi.config.exceptions.InvalidConfigException;
import me.contaria.speedrunapi.config.exceptions.NoSuchConfigException;
import me.contaria.speedrunapi.config.exceptions.SpeedrunConfigAPIException;
import me.contaria.speedrunapi.config.option.CustomFieldBasedOption;
import me.contaria.speedrunapi.config.screen.SpeedrunConfigScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;

public final class SpeedrunConfigAPI {
    private static final EnumMap<Config.InitPoint, TreeSet<SpeedrunConfigContainer.Uninitialized<?>>> CONFIGS_TO_INITIALIZE = new EnumMap<>(Config.InitPoint.class);
    private static final Map<String, SpeedrunConfigContainer<?>> CONFIGS = Collections.synchronizedMap(new HashMap<>());
    private static final Map<String, SpeedrunConfigScreenProvider> CUSTOM_CONFIG_SCREENS = Collections.synchronizedMap(new HashMap<>());

    private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("mcsr");
    private static final Path GLOBAL_CONFIG_DIR = Optional.ofNullable(System.getenv("XDG_CONFIG_HOME")).map(Paths::get).orElse(Paths.get(System.getProperty("user.home"), ".config")).resolve("mcsr").resolve("config");

    static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls().create();

    @SuppressWarnings("unchecked")
    @ApiStatus.Internal
    public static void initialize() {
        List<ModContainer> mods = new ArrayList<>(FabricLoader.getInstance().getAllMods());
        mods.sort(Comparator.comparing(modContainer -> modContainer.getMetadata().getId()));
        for (ModContainer mod : mods) {
            ModMetadata metadata = mod.getMetadata();
            String modID = metadata.getId();
            try {
                CustomValue customValues = metadata.getCustomValue("speedrunapi");
                if (customValues == null) {
                    continue;
                }
                CustomValue.CvObject customObject = customValues.getAsObject();

                CustomValue config = customObject.get("config");
                if (config != null) {
                    Class<?> configClass = Class.forName(config.getAsString());
                    if (!SpeedrunConfig.class.isAssignableFrom(configClass)) {
                        throw new InitializeConfigException("Provided config class from " + modID + " does not implement SpeedrunConfig.");
                    }

                    Config configAnnotation = configClass.getAnnotation(Config.class);

                    Config.InitPoint init;
                    int priority;
                    if (configAnnotation != null) {
                        init = configAnnotation.init();
                        priority = configAnnotation.priority();
                    } else {
                        init = Config.InitPoint.ONINITIALIZE;
                        priority = 1000;
                    }

                    CONFIGS_TO_INITIALIZE.computeIfAbsent(
                            init, key -> new TreeSet<>()
                    ).add(
                            new SpeedrunConfigContainer.Uninitialized<>(
                                    (Class<? extends SpeedrunConfig>) configClass,
                                    mod,
                                    priority
                            )
                    );
                }

                CustomValue screen = customObject.get("screen");
                if (screen != null) {
                    if (config != null) {
                        throw new InitializeConfigException("Tried to provide both a SpeedrunConfig and a SpeedrunConfigScreenProvider for " + modID + ".");
                    }
                    Class<?> screenProviderClass = Class.forName(screen.getAsString());
                    if (!SpeedrunConfigScreenProvider.class.isAssignableFrom(screenProviderClass)) {
                        throw new InitializeConfigException("Provided config screen provider class from " + modID + " does not implement SpeedrunConfigScreenProvider.");
                    }
                    CUSTOM_CONFIG_SCREENS.put(modID, (SpeedrunConfigScreenProvider) constructClass(screenProviderClass));
                }
            } catch (ClassCastException e) {
                throw new InitializeConfigException("Faulty fabric.mod.json values from " + modID + ".", e);
            } catch (ClassNotFoundException e) {
                throw new InitializeConfigException("Provided class from " + modID + " does not exist.", e);
            } catch (ReflectiveOperationException e) {
                throw new InitializeConfigException("Failed to initialize config for " + modID + ".", e);
            }
        }
    }

    @ApiStatus.Internal
    public static void onPreLaunch() {
        initialize();

        registerConfigsForInitPoint(Config.InitPoint.PRELAUNCH);
    }

    @ApiStatus.Internal
    public static void onInitialize() {
        registerConfigsForInitPoint(Config.InitPoint.ONINITIALIZE);
    }

    @ApiStatus.Internal
    public static void onPostLaunch() {
        registerConfigsForInitPoint(Config.InitPoint.POSTLAUNCH);
    }

    private static void registerConfigsForInitPoint(Config.InitPoint initPoint) {
        Set<SpeedrunConfigContainer.Uninitialized<?>> configsToInitialize = CONFIGS_TO_INITIALIZE.get(initPoint);
        if (configsToInitialize != null) {
            configsToInitialize.forEach(SpeedrunConfigAPI::register);
            configsToInitialize.clear();
        }
    }

    private static <T extends SpeedrunConfig> void register(SpeedrunConfigContainer.Uninitialized<T> configUninitialized) {
        ModContainer mod = configUninitialized.mod;
        String modID = mod.getMetadata().getId();

        if (CONFIGS.containsKey(modID)) {
            throw new InitializeConfigException("Config for " + modID + " is already registered!");
        }

        try {
            T config = constructClass(configUninitialized.config);
            SpeedrunConfigContainer<T> container = new SpeedrunConfigContainer<>(config, mod);
            if (!modID.equals(container.getConfig().modID())) {
                throw new InvalidConfigException("The provided SpeedrunConfig's mod ID (" + container.getConfig().modID() + ") doesn't match the providers mod ID (" + modID + ").");
            }
            CONFIGS.put(modID, container);
            config.finishInitialization(container);
        } catch (ReflectiveOperationException e) {
            throw new InitializeConfigException("Failed to build config for " + modID + ".", e);
        }
    }

    private static <T> T constructClass(Class<T> aClass) throws ReflectiveOperationException {
        Constructor<T> constructor = aClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    private static SpeedrunConfigContainer<?> getConfig(String modID) throws NoSuchConfigException {
        SpeedrunConfigContainer<?> config = CONFIGS.get(modID);
        if (config == null) {
            throw new NoSuchConfigException("No config found for mod id " + modID + ".");
        }
        return config;
    }

    /**
     * @return Returns the config directory for MCSR mods.
     * @throws SpeedrunConfigAPIException If an {@link IOException} occurs while trying to create the directory.
     * @implNote Creates the directory if it doesn't currently exist.
     */
    public static Path getConfigDir() throws SpeedrunConfigAPIException {
        if (!Files.exists(CONFIG_DIR)) {
            try {
                Files.createDirectories(CONFIG_DIR);
            } catch (IOException e) {
                throw new SpeedrunConfigAPIException("Failed to create speedrun config directory.", e);
            }
        }
        return CONFIG_DIR;
    }

    /**
     * @return Returns the global config directory for MCSR mods.
     * @throws SpeedrunConfigAPIException If an {@link IOException} occurs while trying to create the directory.
     * @implNote Creates the directory if it doesn't currently exist.
     */
    public static Path getGlobalConfigDir() throws SpeedrunConfigAPIException {
        if (!Files.exists(GLOBAL_CONFIG_DIR)) {
            try {
                Files.createDirectories(GLOBAL_CONFIG_DIR);
            } catch (IOException e) {
                throw new SpeedrunConfigAPIException("Failed to create global speedrun config directory.", e);
            }
        }
        return GLOBAL_CONFIG_DIR;
    }

    /**
     * Retrieves the requested option's value from the {@link SpeedrunConfig} linked to the given mod ID.
     *
     * @param modID  - The mod ID of the mod owning the option.
     * @param option - The name of the option.
     * @return Returns the option's value.
     * @throws NoSuchConfigException - If the given mod does not exist, does not provide a {@link SpeedrunConfig} or does not have the requested option.
     */
    public static Object getConfigValue(String modID, String option) throws NoSuchConfigException {
        return getConfig(modID).getOption(option).get();
    }

    /**
     * Wraps the result of {@link SpeedrunConfigAPI#getConfigValue} in an {@link Optional}.
     * Returns {@link Optional#empty} if a {@link NoSuchConfigException} is thrown or the option is null.
     * <p>
     * This will not catch any other {@link SpeedrunConfigAPIException}'s that may be thrown.
     *
     * @param modID  - The mod ID of the mod owning the option.
     * @param option - The name of the option.
     * @return Returns an {@link Optional} of the option's value.
     * @see SpeedrunConfigAPI#getConfigValue
     */
    @SuppressWarnings("unused")
    public static Optional<Object> getConfigValueOptionally(String modID, String option) {
        try {
            return Optional.ofNullable(getConfigValue(modID, option));
        } catch (NoSuchConfigException e) {
            return Optional.empty();
        }
    }

    /**
     * Sets the requested option's value from the {@link SpeedrunConfig} linked to the given mod ID.
     *
     * @param modID  - The mod ID of the mod owning the option.
     * @param option - The name of the option.
     * @param value  - The value to set the option to.
     * @throws NoSuchConfigException - If the given mod does not exist, does not provide a {@link SpeedrunConfig} or does not have the requested option.
     */
    public static void setConfigValue(String modID, String option, Object value) throws NoSuchConfigException {
        getConfig(modID).getOption(option).setUnsafely(value);
    }

    /**
     * Calls {@link SpeedrunConfigAPI#setConfigValue}.
     * Returns {@code true} if the option is set successfully, {@code false} if a {@link NoSuchConfigException} is thrown.
     * <p>
     * This will not catch any other {@link SpeedrunConfigAPIException}'s that may be thrown.
     *
     * @param modID  - The mod ID of the mod owning the option.
     * @param option - The name of the option.
     * @param value  - The value to set the option to.
     * @see SpeedrunConfigAPI#setConfigValue
     */
    @SuppressWarnings("unused")
    public static boolean setConfigValueOptionally(String modID, String option, Object value) {
        try {
            setConfigValue(modID, option, value);
            return true;
        } catch (NoSuchConfigException e) {
            return false;
        }
    }

    @ApiStatus.Internal
    public static Map<ModContainer, SpeedrunConfigScreenProvider> getModConfigScreenProviders() {
        Map<ModContainer, SpeedrunConfigScreenProvider> configScreenProviders = new TreeMap<>(Comparator.comparing(mod -> mod.getMetadata().getName()));
        CUSTOM_CONFIG_SCREENS.forEach((modID, configScreenProvider) -> configScreenProviders.put(FabricLoader.getInstance().getModContainer(modID).orElseThrow(IllegalStateException::new), configScreenProvider));
        CONFIGS.forEach((modID, config) -> {
            if (config.getConfig().hasConfigScreen()) {
                configScreenProviders.putIfAbsent(config.getModContainer(), config.getConfig());
            }
        });
        return configScreenProviders;
    }

    @ApiStatus.Internal
    public static Screen createDefaultModConfigScreen(String modID, @Nullable Predicate<InputUtil.Key> inputListener, Screen parent) {
        return new SpeedrunConfigScreen(getConfig(modID), inputListener, parent);
    }

    @SuppressWarnings("unused")
    public static class CustomOption {
        public static <T> SpeedrunOption<T> create(SpeedrunConfig config, SpeedrunConfigStorage configStorage, Field optionField, String[] idPrefix, Getter<T> getter, Setter<T> setter, Deserializer<T> fromJson, Serializer<T> toJson, @Nullable WidgetProvider<T> createWidget) {
            return new CustomFieldBasedOption<>(config, configStorage, optionField, idPrefix, getter, setter, fromJson, toJson, createWidget, false);
        }

        public static <T> SpeedrunOption<T> create(SpeedrunConfig config, SpeedrunConfigStorage configStorage, Field optionField, String[] idPrefix, Getter<T> getter, Setter<T> setter, Deserializer<T> fromJson, Serializer<T> toJson, @Nullable WidgetProvider<T> createWidget, boolean hasDefault) {
            return new CustomFieldBasedOption<>(config, configStorage, optionField, idPrefix, getter, setter, fromJson, toJson, createWidget, hasDefault);
        }

        public static class Builder<T> {
            private final SpeedrunConfig config;
            private final SpeedrunConfigStorage configStorage;
            private final Field optionField;
            private final String[] idPrefix;

            @SuppressWarnings("unchecked")
            private Getter<T> getter = (option, config, configStorage, optionField) -> (T) optionField.get(configStorage);
            private Setter<T> setter = (option, config, configStorage, optionField, value) -> optionField.set(configStorage, value);
            @SuppressWarnings("unchecked")
            private Deserializer<T> fromJson = (option, config, configStorage, optionField, jsonElement) -> option.set((T) GSON.fromJson(jsonElement, optionField.getType()));
            private Serializer<T> toJson = (option, config, configStorage, optionField) -> GSON.toJsonTree(option.get(), optionField.getType());
            @Nullable
            private WidgetProvider<T> createWidget;
            private boolean hasDefault = false;

            public Builder(SpeedrunConfig config, SpeedrunConfigStorage configStorage, Field optionField, String... idPrefix) {
                this.config = config;
                this.configStorage = configStorage;
                this.optionField = optionField;
                this.idPrefix = idPrefix;
            }

            public Builder<T> getter(Getter<T> getter) {
                this.getter = getter;
                return this;
            }

            public Builder<T> setter(Setter<T> setter) {
                this.setter = setter;
                return this;
            }

            public Builder<T> fromJson(Deserializer<T> fromJson) {
                this.fromJson = fromJson;
                return this;
            }

            public Builder<T> toJson(Serializer<T> toJson) {
                this.toJson = toJson;
                return this;
            }

            public Builder<T> createWidget(WidgetProvider<T> createWidget) {
                this.createWidget = createWidget;
                return this;
            }

            public Builder<T> hasDefault() {
                this.hasDefault = true;
                return this;
            }

            public SpeedrunOption<T> build() {
                return create(this.config, this.configStorage, this.optionField, this.idPrefix, this.getter, this.setter, this.fromJson, this.toJson, this.createWidget, this.hasDefault);
            }
        }

        @FunctionalInterface
        public interface Getter<T> {
            T get(SpeedrunOption<T> option, SpeedrunConfig config, SpeedrunConfigStorage configStorage, Field optionField) throws ReflectiveOperationException;
        }

        @FunctionalInterface
        public interface Setter<T> {
            void set(SpeedrunOption<T> option, SpeedrunConfig config, SpeedrunConfigStorage configStorage, Field optionField, T value) throws ReflectiveOperationException;
        }

        @FunctionalInterface
        public interface Deserializer<T> {
            void fromJson(SpeedrunOption<T> option, SpeedrunConfig config, SpeedrunConfigStorage configStorage, Field optionField, JsonElement jsonElement);
        }

        @FunctionalInterface
        public interface Serializer<T> {
            JsonElement toJson(SpeedrunOption<T> option, SpeedrunConfig config, SpeedrunConfigStorage configStorage, Field optionField);
        }

        @FunctionalInterface
        public interface WidgetProvider<T> {
            AbstractButtonWidget createWidget(SpeedrunOption<T> option, SpeedrunConfig config, SpeedrunConfigStorage configStorage, Field optionField);
        }
    }
}
