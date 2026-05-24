package me.contaria.speedrunapi.config.api.option;

import me.contaria.speedrunapi.config.screen.SpeedrunConfigScreen;
import net.minecraft.network.chat.Component;

/**
 * Provides names to be used for {@link Enum} option values in the {@link SpeedrunConfigScreen}.
 */
public interface EnumTextProvider {

    /**
     * @return Returns name for the {@link Enum} value.
     */
    Component toText();
}
