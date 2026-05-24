package me.contaria.speedrunapi.config.screen.widgets.option;

import me.contaria.speedrunapi.config.option.StringOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class StringOptionTextFieldWidget extends EditBox {

    public StringOptionTextFieldWidget(StringOption option, int x, int y) {
        super(Minecraft.getInstance().font, x, y, 150, 20, option.getName());
        this.setMaxLength(option.getMaxLength());
        this.setValue(option.get());
        this.setResponder(option::set);
    }
}
