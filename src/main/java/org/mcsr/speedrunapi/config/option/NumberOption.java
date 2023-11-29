package org.mcsr.speedrunapi.config.option;

import org.mcsr.speedrunapi.config.api.SpeedrunConfig;
import org.mcsr.speedrunapi.config.api.SpeedrunConfigStorage;
import org.mcsr.speedrunapi.config.api.annotations.Config;

import java.lang.reflect.Field;

public abstract class NumberOption<T extends Number> extends FieldBasedOption<T> {

    protected final boolean useTextField;

    public NumberOption(SpeedrunConfig config, SpeedrunConfigStorage configStorage, Field option, String... idPrefix) {
        super(config, configStorage, option, idPrefix);

        this.useTextField = option.isAnnotationPresent(Config.Numbers.TextField.class);
    }

    public abstract void setFromSliderValue(double sliderValue);

    public abstract void setFromString(String stringValue) throws NumberFormatException;
}
