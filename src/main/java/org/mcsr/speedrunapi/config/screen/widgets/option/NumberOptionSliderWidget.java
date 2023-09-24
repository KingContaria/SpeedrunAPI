package org.mcsr.speedrunapi.config.screen.widgets.option;

import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.LiteralText;
import org.mcsr.speedrunapi.config.option.NumberOption;

public abstract class NumberOptionSliderWidget<T extends NumberOption<?>> extends SliderWidget {

    protected final T option;

    public NumberOptionSliderWidget(T option, int x, int y, double value) {
        super(x, y, 150, 20, LiteralText.EMPTY, value);
        this.option = option;
        this.updateValue();
    }

    protected abstract void updateValue();
}
