package org.mcsr.speedrunapi.config.screen.widgets.option;

import net.minecraft.text.LiteralText;
import org.mcsr.speedrunapi.config.option.FractionalNumberOption;

public class FractionalNumberOptionSliderWidget<T extends Number> extends NumberOptionSliderWidget<FractionalNumberOption<T>> {

    public FractionalNumberOptionSliderWidget(FractionalNumberOption<T> option, int x, int y) {
        super(option, x, y, ((double) option.get() - option.getMin()) / (option.getMax() - option.getMin()));
    }

    @Override
    protected void updateMessage() {
        this.setMessage(new LiteralText(String.valueOf(Math.round(this.option.get().doubleValue() * 100.0) / 100.0)));
    }

    @Override
    protected void applyValue() {
        this.option.setFromSliderValue(this.value);
    }
}
