package org.mcsr.speedrunapi.config.api;

import net.minecraft.text.Text;
import org.mcsr.speedrunapi.config.screen.SpeedrunConfigScreen;

/**
 * Provides names to be used for {@link Enum} options in the {@link SpeedrunConfigScreen}.
 */
public interface EnumTextProvider {

    /**
     * @return Returns name for the {@link Enum} value.
     */
    Text toText();
}
