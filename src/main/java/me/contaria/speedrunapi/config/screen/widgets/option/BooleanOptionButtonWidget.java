package me.contaria.speedrunapi.config.screen.widgets.option;

import me.contaria.speedrunapi.config.option.BooleanOption;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class BooleanOptionButtonWidget extends ButtonWidget {

    public BooleanOptionButtonWidget(BooleanOption option, int x, int y) {
        super(x, y, 150, 20, option.getText(), button -> {
            option.set(!option.get());
            button.setMessage(option.getText());
        });
    }
}
