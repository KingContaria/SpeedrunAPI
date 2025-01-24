package org.mcsr.speedrunapi.config.api;

import com.google.gson.JsonObject;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mcsr.speedrunapi.config.SpeedrunConfigAPI;
import org.mcsr.speedrunapi.config.SpeedrunConfigContainer;

import java.io.File;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Provides a custom config screen, can also be used by mods using their own config system to show up in the config list.
 * <p>
 * Register by adding your class implementing {@link SpeedrunConfig} to your mod's fabric.mod.json like this:
 * <p>
 * "custom": [ "speedrunapi": [ "config": "a.b.c.ABCConfig" ] ]
 */
public interface SpeedrunConfig extends SpeedrunConfigStorage, SpeedrunConfigScreenProvider {

    /**
     * @return Returns the mod ID of the mod owning the config.
     */
    String modID();

    /**
     * Initializes the config, creating all the {@link SpeedrunOption}'s it provides.
     *
     * @return Returns a {@link Map} of all of this configs {@link SpeedrunOption}'s mapped to their ID's.
     */
    default Map<String, SpeedrunOption<?>> init() throws ReflectiveOperationException {
        return this.init(this);
    }

    /**
     * Gets called when the config has finished initialization and has been registered.
     *
     * @param container - The {@link SpeedrunConfigContainer} containing the options for this {@link SpeedrunConfig}.
     */
    default void finishInitialization(SpeedrunConfigContainer<?> container) {
    }

    /**
     * Gets called before the config starts loading.
     *
     * @apiNote The config may or may not have finished initialization at this point.
     */
    default void preLoad() {
    }

    /**
     * Gets called after the config has been loaded from disk, and before it loads the options from JSON.
     * This method may be used to convert older versions of the config file or.
     *
     * @param jsonObject - The config JSON that is about to be loaded.
     * @param dataVersion - The data version this JSON was saved as.
     */
    default void onLoad(JsonObject jsonObject, int dataVersion) {
    }

    /**
     * Gets called when the config has successfully finished loading.
     *
     * @apiNote The config may or may not have finished initialization at this point.
     */
    default void finishLoading() {
    }

    /**
     * Gets called before the config starts saving.
     *
     * @apiNote The config may or may not have finished initialization at this point.
     */
    default void preSave() {
    }

    default void onSave(JsonObject jsonObject) {
    }

    /**
     * Gets called when the config has successfully finished saving.
     *
     * @apiNote The config may or may not have finished initialization at this point.
     */
    default void finishSaving() {
    }

    /**
     * Mod Authors can override this method to change the config file location, for example to add global config files.
     *
     * @apiNote The directory of the returned file gets created by this method.
     * @return Returns the file the config should be saved to.
     */
    default File getConfigFile() {
        return SpeedrunConfigAPI.getConfigDir().resolve(this.modID() + ".json").toFile();
    }

    default int getDataVersion() {
        return 0;
    }

    /**
     * @return Returns {@code true} if this config should show a config screen on the SpeedrunAPI Mod Configs menu.
     */
    default boolean hasConfigScreen() {
        return true;
    }

    @Override
    default @NotNull Screen createConfigScreen(Screen parent) {
        return SpeedrunConfigAPI.createDefaultModConfigScreen(this.modID(), this.createInputListener(), parent);
    }

    /**
     * Mod Authors can override this method to add custom key or mouse press functionality to their config screen.
     * <p>
     * Creates a {@link Predicate} that listens for inputs on the default config screen.
     * Returning {@code true} prevents the input from being sent to the screen.
     *
     * @return Returns an input listener for the default config screen.
     */
    default @Nullable Predicate<InputUtil.Key> createInputListener() {
        return null;
    }

     /**
     * Mod Authors can override this method to dynamically hide categories from the default config screen.
     *
     * @param category - The categories ID.
     * @return Returns {@code true} if the given category should be shown on the config screen.
     */
    default boolean shouldShowCategory(String category) {
        return true;
    }

    /**
     * Mod Authors can override this method to dynamically hide options from the default config screen.
     *
     * @param option - The options ID.
     * @return Returns {@code true} if the given option should be shown on the config screen.
     */
    default boolean shouldShowOption(String option) {
        return true;
    }
}
