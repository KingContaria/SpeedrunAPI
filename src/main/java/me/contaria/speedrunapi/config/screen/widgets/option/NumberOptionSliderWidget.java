package me.contaria.speedrunapi.config.screen.widgets.option;

import me.contaria.speedrunapi.config.option.NumberOption;
import me.contaria.speedrunapi.util.TextUtil;
import net.minecraft.client.gui.widget.SliderWidget;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public abstract class NumberOptionSliderWidget<T extends NumberOption<?>> extends SliderWidget {
    protected final T option;

    public NumberOptionSliderWidget(T option, int x, int y, double value) {
        super(x, y, 150, 20, TextUtil.empty(), value);
        this.option = option;
        this.updateValue();
    }

    @Override
    protected void updateMessage() {
        this.setMessage(this.option.getText());
    }

    @Override
    protected void applyValue() {
        this.option.setFromSliderValue(this.value);
    }

    protected abstract void updateValue();
}
