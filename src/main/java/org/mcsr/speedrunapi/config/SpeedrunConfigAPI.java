package org.mcsr.speedrunapi.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import org.mcsr.speedrunapi.config.api.SpeedrunConfigScreenProvider;
import org.mcsr.speedrunapi.config.api.annotations.SpeedrunConfig;

import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.util.*;

public class SpeedrunConfigAPI {

    private static final EnumMap<SpeedrunConfig.InitPoint, Map<ModContainer, Class<?>>> CONFIGS_TO_INITIALIZE = new EnumMap<>(SpeedrunConfig.InitPoint.class);

    private static final Map<String, SpeedrunConfigContainer<?>> CONFIGS = Collections.synchronizedMap(new HashMap<>());
    private static final Map<String, SpeedrunConfigScreenProvider> CUSTOM_CONFIG_SCREENS = Collections.synchronizedMap(new HashMap<>());
    protected static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("mcsr");
    protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void initialize() {
        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            try {
                CustomValue customValues = mod.getMetadata().getCustomValues().get("speedrunapi");
                if (customValues != null) {
                    CustomValue.CvObject customObject = customValues.getAsObject();

                    CustomValue config = customObject.get("config");
                    if (config != null) {
                        Class<?> configClass = Class.forName(config.getAsString());
                        SpeedrunConfig configAnnotation = configClass.getAnnotation(SpeedrunConfig.class);
                        if (configAnnotation != null) {
                            if (configAnnotation.modID().equals(mod.getMetadata().getId())) {
                                CONFIGS_TO_INITIALIZE.computeIfAbsent(configAnnotation.initializeOn(), initPoint -> new HashMap<>()).put(mod, configClass);
                            } else {
                                throw new RuntimeException("The provided @SpeedrunConfig's mod ID doesnt match.");
                            }
                        } else {
                            throw new RuntimeException("Provided config class from " + mod.getMetadata().getId() + " is not annotated with @SpeedrunConfig.");
                        }
                    }

                    CustomValue screen = customObject.get("screen");
                    if (screen != null) {
                        Class<?> screenProviderClass = Class.forName(screen.getAsString());
                        if (SpeedrunConfigScreenProvider.class.isAssignableFrom(screenProviderClass)) {
                            CUSTOM_CONFIG_SCREENS.put(mod.getMetadata().getId(), (SpeedrunConfigScreenProvider) constructClass(screenProviderClass));
                        } else {
                            throw new RuntimeException("Provided config screen provider class from " + mod.getMetadata().getId() + " does not implement SpeedrunConfigScreenProvider.");
                        }
                    }
                }
            } catch (ClassCastException e) {
                throw new RuntimeException("Faulty fabric.mod.json values from " + mod.getMetadata().getId() + ".", e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Provided class from " + mod.getMetadata().getId() + " does not exist.", e);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void onPreLaunch() {
        initialize();

        registerConfigsForInitPoint(SpeedrunConfig.InitPoint.PRELAUNCH);
    }

    public static void onInitialize() {
        registerConfigsForInitPoint(SpeedrunConfig.InitPoint.ONINITIALIZE);
    }

    public static void onPostLaunch() {
        registerConfigsForInitPoint(SpeedrunConfig.InitPoint.POSTLAUNCH);
    }

    private static void registerConfigsForInitPoint(SpeedrunConfig.InitPoint initPoint) {
        Map<ModContainer, Class<?>> configsToInitialize = CONFIGS_TO_INITIALIZE.get(initPoint);
        if (configsToInitialize != null) {
            configsToInitialize.forEach(SpeedrunConfigAPI::register);
            configsToInitialize.clear();
        }
    }

    private static <T> void register(ModContainer mod, Class<T> configClass) {
        String modID = mod.getMetadata().getId();

        if (CONFIGS.containsKey(modID)) {
            throw new RuntimeException("Config for " + modID + " is already registered!");
        }

        try {
            SpeedrunConfigContainer<T> config = new SpeedrunConfigContainer<>(constructClass(configClass), mod);
            CONFIGS.put(modID, config);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to build config for " + modID, e);
        }
    }

    private static <T> T constructClass(Class<T> aClass) throws ReflectiveOperationException {
        Constructor<T> constructor = aClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    public static SpeedrunConfigContainer<?> getConfig(String modID) {
        return CONFIGS.get(modID);
    }

    public static Map<ModContainer, SpeedrunConfigScreenProvider> getModConfigScreenProviders() {
        Map<ModContainer, SpeedrunConfigScreenProvider> configScreenProviders = new HashMap<>();
        CUSTOM_CONFIG_SCREENS.forEach((modID, configScreenProvider) -> configScreenProviders.put(FabricLoader.getInstance().getModContainer(modID).orElseThrow(RuntimeException::new), configScreenProvider));
        CONFIGS.forEach((modID, config) -> configScreenProviders.putIfAbsent(FabricLoader.getInstance().getModContainer(modID).orElseThrow(RuntimeException::new), config::createConfigScreen));
        return configScreenProviders;
    }
}
