package org.mcsr.speedrunapi.config.screen.widgets.option;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.LiteralText;
import org.mcsr.speedrunapi.config.option.StringOption;

public class StringOptionTextFieldWidget extends TextFieldWidget {

    public StringOptionTextFieldWidget(StringOption option, int x, int y) {
        super(MinecraftClient.getInstance().textRenderer, x, y, 150, 20, LiteralText.EMPTY);
        this.setMaxLength(option.getMaxLength());
        this.setText(option.get());
        this.setChangedListener(option::set);
    }
}
