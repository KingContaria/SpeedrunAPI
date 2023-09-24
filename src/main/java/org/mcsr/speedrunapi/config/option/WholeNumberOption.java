package org.mcsr.speedrunapi.config.option;

import net.minecraft.client.gui.widget.AbstractButtonWidget;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mcsr.speedrunapi.config.api.annotations.Config;
import org.mcsr.speedrunapi.config.screen.widgets.option.WholeNumberOptionSliderWidget;

import java.lang.reflect.Field;

public abstract class WholeNumberOption<T extends Number> extends NumberOption<T> {

    @NotNull
    private final Config.Numbers.Whole.Bounds bounds;

    @Nullable
    private final Config.Numbers.Whole.Intervals intervals;

    public WholeNumberOption(Object config, Field option) {
        super(config, option);

        this.bounds = option.getAnnotation(Config.Numbers.Whole.Bounds.class);
        if (this.bounds == null) {
            throw new RuntimeException("Missing WholeBounds annotation on " + this.getID() + " config field!");
        }
        if (this.getMax() <= this.getMin()) {
            throw new RuntimeException("Invalid bounds for " + this.getID() + "! Min: " + this.getMin() + ", Max: " + this.getMax());
        }

        this.intervals = option.getAnnotation(Config.Numbers.Whole.Intervals.class);
        long intervals = this.getIntervals();
        if (intervals != 0L && (this.getMax() - this.getMin() % intervals) != 0) {
            throw new RuntimeException("Invalid intervals for " + this.getID() + "! Intervals: " + intervals + ", Min: " + this.getMin() + ", Max: " + this.getMax());
        }
    }

    @Override
    public AbstractButtonWidget createWidget() {
        return new WholeNumberOptionSliderWidget<>(this, 0, 0);
    }

    @Override
    public void setFromSliderValue(double sliderValue) {
        long min = this.getMin();
        long max = this.getMax();
        this.setLong(Math.round(min + (max - min) * sliderValue));
        this.get();
    }

    public long getMin() {
        return this.bounds.min();
    }

    public long getMax() {
        return this.bounds.max();
    }

    public long getIntervals() {
        return this.intervals != null ? this.intervals.value() : 0L;
    }

    public abstract void setLong(long value);
}
