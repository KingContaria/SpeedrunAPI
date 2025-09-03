package me.contaria.speedrunapi.config.screen.widgets.option;

import me.contaria.speedrunapi.config.api.gui.CallbackButtonWidget;
import me.contaria.speedrunapi.config.option.EnumOption;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class EnumOptionButtonWidget extends CallbackButtonWidget {

    public EnumOptionButtonWidget(EnumOption option) {
        super(option.getText(), button -> {
            Enum<?> current = option.get();
            Enum<?>[] enumConstants = current.getClass().getEnumConstants();
            option.set(enumConstants[(current.ordinal() + 1) % enumConstants.length]);
            button.message = option.getText();
        });
    }
}
