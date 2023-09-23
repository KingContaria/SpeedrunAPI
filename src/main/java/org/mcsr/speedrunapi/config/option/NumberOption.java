package org.mcsr.speedrunapi.config.option;

import java.lang.reflect.Field;

public abstract class NumberOption<T extends Number> extends Option<T> {

    public NumberOption(Object config, Field option) {
        super(config, option);
    }

    public abstract void setFromSliderValue(double sliderValue);
}
