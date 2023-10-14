package org.mcsr.speedrunapi.config.option;

import org.mcsr.speedrunapi.config.api.SpeedrunConfig;

import java.lang.reflect.Field;

public abstract class NumberOption<T extends Number> extends BaseOption<T> {

    public NumberOption(SpeedrunConfig config, Field option) {
        super(config, option);
    }

    public abstract void setFromSliderValue(double sliderValue);

    public abstract void setFromString(String stringValue) throws NumberFormatException;
}
