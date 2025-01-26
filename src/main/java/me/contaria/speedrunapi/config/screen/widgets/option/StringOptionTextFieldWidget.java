package me.contaria.speedrunapi.config.screen.widgets.option;

import me.contaria.speedrunapi.config.option.StringOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class StringOptionTextFieldWidget extends TextFieldWidget {

    public StringOptionTextFieldWidget(StringOption option, int x, int y) {
        super(MinecraftClient.getInstance().textRenderer, x, y, 150, 20, option.getName());
        this.setMaxLength(option.getMaxLength());
        this.setText(option.get());
        this.setChangedListener(option::set);
    }
}
