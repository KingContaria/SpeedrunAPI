package me.contaria.speedrunapi.config.option;

import me.contaria.speedrunapi.config.api.SpeedrunConfig;
import me.contaria.speedrunapi.config.api.SpeedrunConfigStorage;
import me.contaria.speedrunapi.config.api.annotations.Config;
import me.contaria.speedrunapi.config.exceptions.InvalidConfigException;
import me.contaria.speedrunapi.config.screen.widgets.option.FractionalNumberOptionSliderWidget;
import me.contaria.speedrunapi.config.screen.widgets.option.NumberOptionTextFieldWidget;
import me.contaria.speedrunapi.util.TextUtil;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

@ApiStatus.Internal
public abstract class FractionalNumberOption<T extends Number> extends NumberOption<T> {
    @NotNull
    protected final Config.Numbers.Fractional.Bounds bounds;

    @Override
    public @NotNull Text getDefaultText() {
        return TextUtil.literal(String.valueOf(Math.round(this.get().doubleValue() * 100.0) / 100.0));
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
