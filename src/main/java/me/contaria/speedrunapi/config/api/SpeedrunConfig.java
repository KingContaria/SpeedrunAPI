package me.contaria.speedrunapi.config.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.contaria.speedrunapi.config.SpeedrunConfigAPI;
import me.contaria.speedrunapi.config.SpeedrunConfigContainer;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
     * @param jsonObject  - The config JSON that is about to be loaded.
     * @param metadata - The metadata this JSON was saved with.
     */
    default void onLoad(JsonObject jsonObject, SpeedrunConfigParsedMetadata metadata) {
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
     * Gets called when the config screen closes normally, e.g. when pressing the done button or Esc, but not after an Atum hotkey reset.
     *
     * @param current - The current open config screen.
     * @param parent  - The parent screen of the open config screen.
     */
    default void onConfigScreenClose(Screen current, Screen parent) {
    }

    /**
     * Mod Authors can override this method to change the config file location, for example to add global config files.
     *
     * @return Returns the file the config should be saved to.
     * @apiNote The directory of the returned file gets created by this method.
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
    default @Nullable Predicate<Integer> createInputListener() {
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

    /**
     * @return If static fields in the {@link SpeedrunConfig} or any related {@link SpeedrunConfigStorage} should be parsed as options.
     */
    default boolean shouldParseStaticFields() {
        return false;
    }

    /**
     * Handles any exceptions thrown during config loading, including those thrown in {@link SpeedrunConfig#handleLoadException(Exception, SpeedrunOption, JsonElement)}.
     * <p>
     * This includes {@link SpeedrunConfig#onLoad} but excludes {@link SpeedrunConfig#preLoad} and {@link SpeedrunConfig#finishLoading}.
     */
    default void handleLoadException(Exception e) throws Exception {
        SpeedrunConfigAPI.handleLoadException(this.modID(), e);
    }

    /**
     * Handles any exceptions thrown by {@link SpeedrunOption#fromJson} during config loading.
     */
    default void handleLoadException(Exception e, SpeedrunOption<?> option, JsonElement jsonElement) throws Exception {
        throw e;
    }

    /**
     * Handles any exceptions thrown during config saving, including those thrown in {@link SpeedrunConfig#handleSaveException(Exception, SpeedrunOption)}.
     * <p>
     * This includes {@link SpeedrunConfig#onSave} but excludes {@link SpeedrunConfig#preSave} and {@link SpeedrunConfig#finishSaving}.
     */
    default void handleSaveException(Exception e) throws Exception {
        SpeedrunConfigAPI.handleSaveException(this.modID(), e);
    }

    /**
     * Handles any exceptions thrown by {@link SpeedrunOption#toJson} during config saving.
     */
    default void handleSaveException(Exception e, SpeedrunOption<?> option) throws Exception {
        throw e;
    }
}
