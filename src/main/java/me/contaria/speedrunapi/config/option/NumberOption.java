package me.contaria.speedrunapi.config.option;

import me.contaria.speedrunapi.config.api.SpeedrunConfig;
import me.contaria.speedrunapi.config.api.SpeedrunConfigStorage;
import me.contaria.speedrunapi.config.api.annotations.Config;
import me.contaria.speedrunapi.config.screen.widgets.option.NumberOptionSliderWidget;
import me.contaria.speedrunapi.config.screen.widgets.option.NumberOptionTextFieldWidget;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

@ApiStatus.Internal
public abstract class NumberOption<T extends Number> extends FieldBasedOption<T> {
    protected final boolean useTextField;

    public NumberOption(SpeedrunConfig config, SpeedrunConfigStorage configStorage, Field option, String... idPrefix) {
        super(config, configStorage, option, idPrefix);

        this.useTextField = option.isAnnotationPresent(Config.Numbers.TextField.class);
    }

    public abstract void setFromSliderValue(double sliderValue);

    public abstract void setFromString(String stringValue) throws NumberFormatException;

    public abstract float getSliderMin();

    public abstract float getSliderMax();

    @Override
    public @NotNull Object createWidget() {
        if (this.useTextField) {
            return new NumberOptionTextFieldWidget<>(this);
        }
        return new NumberOptionSliderWidget<>(this);
    }
}
