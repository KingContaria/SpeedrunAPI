package me.contaria.speedrunapi.config.screen.widgets.option;

import me.contaria.speedrunapi.config.api.gui.CallbackButtonWidget;
import me.contaria.speedrunapi.config.option.BooleanOption;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class BooleanOptionButtonWidget extends CallbackButtonWidget {

    public BooleanOptionButtonWidget(BooleanOption option) {
        super(option.getText(), button -> {
            option.set(!option.get());
            button.message = option.getText();
        });
    }
}
