package org.mcsr.speedrunapi.config.screen.widgets.option;

import net.minecraft.client.gui.widget.ButtonWidget;
import org.jetbrains.annotations.ApiStatus;
import org.mcsr.speedrunapi.config.option.BooleanOption;

@ApiStatus.Internal
public class BooleanOptionButtonWidget extends ButtonWidget {

    public BooleanOptionButtonWidget(BooleanOption option, int x, int y) {
        super(x, y, 150, 20, option.getText(), button -> {
            option.set(!option.get());
            button.setMessage(option.getText());
        });
    }
}
