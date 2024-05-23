package org.mcsr.speedrunapi.config.screen.widgets.option;

import net.minecraft.client.gui.widget.ButtonWidget;
import org.jetbrains.annotations.ApiStatus;
import org.mcsr.speedrunapi.config.option.EnumOption;

@ApiStatus.Internal
public class EnumOptionButtonWidget extends ButtonWidget {

    public EnumOptionButtonWidget(EnumOption option, int x, int y) {
        super(x, y, 150, 20, option.getText(), button -> {
            Enum<?> current = option.get();
            Enum<?>[] enumConstants = current.getClass().getEnumConstants();
            option.set(enumConstants[(current.ordinal() + 1) % enumConstants.length]);
            button.setMessage(option.getText());
        });
    }
}
