package org.mcsr.speedrunapi.config.screen.widgets.option;

import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.LiteralText;
import org.jetbrains.annotations.ApiStatus;
import org.mcsr.speedrunapi.config.option.NumberOption;

@ApiStatus.Internal
public abstract class NumberOptionSliderWidget<T extends NumberOption<?>> extends SliderWidget {

    protected final T option;

    public NumberOptionSliderWidget(T option, int x, int y, double value) {
        super(x, y, 150, 20, LiteralText.EMPTY, value);
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
