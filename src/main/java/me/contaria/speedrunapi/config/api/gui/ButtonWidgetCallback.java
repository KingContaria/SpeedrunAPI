package me.contaria.speedrunapi.config.api.gui;

import me.contaria.speedrunapi.config.api.SpeedrunOption;
import net.minecraft.client.gui.widget.ButtonWidget;

/**
 * May be implemented by {@link ButtonWidget}'s returned by {@link SpeedrunOption#createWidget} to define behaviour when pressed.
 */
public interface ButtonWidgetCallback {
    /**
     * Called when the implementing {@link ButtonWidget} is pressed on the default config screen.
     */
    default void onPress() {
    }
}
