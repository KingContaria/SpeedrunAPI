package org.mcsr.speedrunapi.config.option;

import net.minecraft.client.gui.widget.AbstractButtonWidget;
import org.jetbrains.annotations.NotNull;
import org.mcsr.speedrunapi.config.api.annotations.Config;
import org.mcsr.speedrunapi.config.screen.widgets.option.FractionalNumberOptionSliderWidget;

import java.lang.reflect.Field;

public abstract class FractionalNumberOption<T extends Number> extends NumberOption<T> {

    @NotNull
    private final Config.Numbers.FractionalBounds bounds;

    public FractionalNumberOption(Object config, Field option) {
        super(config, option);

        this.bounds = option.getAnnotation(Config.Numbers.FractionalBounds.class);
        if (this.bounds == null) {
            throw new RuntimeException("Missing FractionalBounds annotation on " + this.getID() + " config field!");
        }
        if (this.getMax() <= this.getMin()) {
            throw new RuntimeException("Invalid bounds for " + this.getID() + "! Min: " + this.getMin() + ", Max: " + this.getMax());
        }
    }

    @Override
    public AbstractButtonWidget createWidget() {
        return new FractionalNumberOptionSliderWidget<>(this, 0, 0);
    }

    @Override
    public void setFromSliderValue(double sliderValue) {
        double min = this.getMin();
        double max = this.getMax();
        this.setDouble(min + (max - min) * sliderValue);
        this.get();
    }

    public double getMin() {
        return this.bounds.min();
    }

    public double getMax() {
        return this.bounds.max();
    }

    public abstract void setDouble(double value);
}
