package me.contaria.speedrunapi.config.api;

import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.NotNull;
import me.contaria.speedrunapi.config.screen.SpeedrunModConfigsScreen;

/**
 * Provides a custom config screen, can be used by mods using their own config system to show up in the config list.
 * <p>
 * Register by adding your class implementing {@link SpeedrunConfigScreenProvider} to your mod's fabric.mod.json like this:
 * <p>
 * "custom": [ "speedrunapi": [ "screen": "a.b.c.ABCConfigScreenProvider" ] ]
 */
public interface SpeedrunConfigScreenProvider {

    /**
     * @param parent - The active {@link SpeedrunModConfigsScreen} the config screen is opened from.
     * @return Returns a config screen for the mod providing the {@link SpeedrunConfigScreenProvider}.
     */
    @NotNull Screen createConfigScreen(Screen parent);

    /**
     * Mod Authors can override this method to make the config screen unavailable for players, for example during runs.
     *
     * @return Returns {@code true} if the config screen is available.
     */
    default boolean isAvailable() {
        return true;
    }
}
