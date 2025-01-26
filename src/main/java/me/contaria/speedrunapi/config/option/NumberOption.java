package me.contaria.speedrunapi.config.option;

import me.contaria.speedrunapi.config.api.SpeedrunConfig;
import me.contaria.speedrunapi.config.api.annotations.Config;
import org.jetbrains.annotations.ApiStatus;
import me.contaria.speedrunapi.config.api.SpeedrunConfigStorage;

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
}
