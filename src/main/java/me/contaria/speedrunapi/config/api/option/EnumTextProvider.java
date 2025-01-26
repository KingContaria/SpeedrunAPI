package me.contaria.speedrunapi.config.api.option;

import net.minecraft.text.Text;
import me.contaria.speedrunapi.config.screen.SpeedrunConfigScreen;

/**
 * Provides names to be used for {@link Enum} option values in the {@link SpeedrunConfigScreen}.
 */
public interface EnumTextProvider {

    /**
     * @return Returns name for the {@link Enum} value.
     */
    Text toText();
}
