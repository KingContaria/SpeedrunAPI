package org.mcsr.speedrunapi.config.option;

import net.minecraft.client.gui.widget.AbstractButtonWidget;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mcsr.speedrunapi.config.api.SpeedrunConfig;
import org.mcsr.speedrunapi.config.api.SpeedrunConfigStorage;
import org.mcsr.speedrunapi.config.api.annotations.Config;
import org.mcsr.speedrunapi.config.exceptions.InvalidConfigException;
import org.mcsr.speedrunapi.config.screen.widgets.option.NumberOptionTextFieldWidget;
import org.mcsr.speedrunapi.config.screen.widgets.option.WholeNumberOptionSliderWidget;

import java.lang.reflect.Field;

public abstract class WholeNumberOption<T extends Number> extends NumberOption<T> {

    @NotNull
    protected final Config.Numbers.Whole.Bounds bounds;
    @Nullable
    protected final Config.Numbers.Whole.Intervals intervals;

    public WholeNumberOption(SpeedrunConfig config, SpeedrunConfigStorage configStorage, Field option, String... idPrefix) {
        super(config, configStorage, option, idPrefix);

        this.bounds = option.getAnnotation(Config.Numbers.Whole.Bounds.class);
        if (this.bounds == null) {
            throw new InvalidConfigException("Missing Bounds annotation on " + this.getID() + " config field!");
        }
        if (this.getMax() <= this.getMin()) {
            throw new InvalidConfigException("Invalid bounds for " + this.getID() + "! Min: " + this.getMin() + ", Max: " + this.getMax());
        }

        this.intervals = option.getAnnotation(Config.Numbers.Whole.Intervals.class);
        long intervals = this.getIntervals();
        if (intervals != 0L && (((this.getMax() - this.getMin()) % intervals) != 0L || intervals < 0L)) {
            throw new InvalidConfigException("Invalid intervals for " + this.getID() + "! Intervals: " + intervals + ", Min: " + this.getMin() + ", Max: " + this.getMax());
        }
    }

    @Override
    public boolean hasWidget() {
        return true;
    }

    @Override
    public AbstractButtonWidget createWidget() {
        if (this.useTextField) {
            return new NumberOptionTextFieldWidget<>(this, 0, 0);
        }
        return new WholeNumberOptionSliderWidget<>(this, 0, 0);
    }

    @Override
    public void setFromSliderValue(double sliderValue) {
        long min = this.getMin();
        long max = this.getMax();
        this.setLong(Math.round(min + (max - min) * sliderValue));
    }

    @Override
    public void setFromString(String stringValue) throws NumberFormatException {
        this.setLong(Long.parseLong(stringValue));
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
