package org.mcsr.speedrunapi.config.option;

import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mcsr.speedrunapi.config.api.SpeedrunConfig;
import org.mcsr.speedrunapi.config.api.SpeedrunConfigStorage;
import org.mcsr.speedrunapi.config.api.annotations.Config;
import org.mcsr.speedrunapi.config.exceptions.InvalidConfigException;
import org.mcsr.speedrunapi.config.screen.widgets.option.FractionalNumberOptionSliderWidget;
import org.mcsr.speedrunapi.config.screen.widgets.option.NumberOptionTextFieldWidget;

import java.lang.reflect.Field;

public abstract class FractionalNumberOption<T extends Number> extends NumberOption<T> {

    @NotNull
    protected final Config.Numbers.Fractional.Bounds bounds;

    @Override
    public @NotNull Text getDefaultText() {
        return new LiteralText(String.valueOf(Math.round(this.get().doubleValue() * 100.0) / 100.0));
    }

    @Nullable
    protected final Config.Numbers.Fractional.Intervals intervals;

    public FractionalNumberOption(SpeedrunConfig config, SpeedrunConfigStorage configStorage, Field option, String... idPrefix) {
        super(config, configStorage, option, idPrefix);

        this.bounds = option.getAnnotation(Config.Numbers.Fractional.Bounds.class);
        if (this.bounds == null) {
            throw new InvalidConfigException("Missing Bounds annotation on " + this.getID() + " config field!");
        }
        if (this.getMax() <= this.getMin()) {
            throw new InvalidConfigException("Invalid bounds for " + this.getID() + "! Min: " + this.getMin() + ", Max: " + this.getMax());
        }

        this.intervals = option.getAnnotation(Config.Numbers.Fractional.Intervals.class);
        double intervals = this.getIntervals();
        if (intervals < 0.0) {
            throw new InvalidConfigException("Invalid intervals for " + this.getID() + "! Intervals: " + intervals + ", Min: " + this.getMin() + ", Max: " + this.getMax());
        }
    }

    @Override
    public boolean hasWidget() {
        return true;
    }

    @Override
    public @NotNull AbstractButtonWidget createWidget() {
        if (this.useTextField) {
            return new NumberOptionTextFieldWidget<>(this, 0, 0);
        }
        return new FractionalNumberOptionSliderWidget<>(this, 0, 0);
    }

    @Override
    public void setFromSliderValue(double sliderValue) {
        double min = this.getMin();
        double max = this.getMax();
        this.setDouble(min + (max - min) * sliderValue);
    }

    @Override
    public void setFromString(String stringValue) throws NumberFormatException {
        this.setDouble(Double.parseDouble(stringValue));
    }

    public double getMin() {
        return this.bounds.min();
    }

    public double getMax() {
        return this.bounds.max();
    }

    public double getIntervals() {
        return this.intervals != null ? this.intervals.value() : 0.0;
    }

    public abstract void setDouble(double value);
}
