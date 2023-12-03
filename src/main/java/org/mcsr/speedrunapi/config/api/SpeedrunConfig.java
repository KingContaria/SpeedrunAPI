package org.mcsr.speedrunapi.config.api;

import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.NotNull;
import org.mcsr.speedrunapi.config.SpeedrunConfigAPI;
import org.mcsr.speedrunapi.config.SpeedrunConfigContainer;

import java.io.File;
import java.util.Map;

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
    default Map<String, SpeedrunOption<?>> init() {
        return this.init(this);
    }

    /**
     * Gets called when the config has finished initialization and has been registered.
     *
     * @param container - The {@link SpeedrunConfigContainer} containing the options for this {@link SpeedrunConfig}.
     * @param <THIS> - The mod config class implementing this interface.
     */
    default <THIS extends SpeedrunConfig> void finishInitialization(SpeedrunConfigContainer<THIS> container) {
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

    @Override
    default @NotNull Screen createConfigScreen(Screen parent) {
        return SpeedrunConfigAPI.createDefaultModConfigScreen(this.modID(), parent);
    }
}
