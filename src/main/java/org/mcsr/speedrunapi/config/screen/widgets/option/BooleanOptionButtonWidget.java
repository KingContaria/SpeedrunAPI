package org.mcsr.speedrunapi.config.screen.widgets.option;

import net.minecraft.client.gui.widget.ButtonWidget;
import org.mcsr.speedrunapi.config.option.BooleanOption;

public class BooleanOptionButtonWidget extends ButtonWidget {

    public BooleanOptionButtonWidget(BooleanOption option, int x, int y) {
        super(x, y, 150, 20, option.getText(), button -> {
            option.set(!option.get());
            button.setMessage(option.getText());
        });
    }
}
