package org.mcsr.speedrunapi.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.mcsr.speedrunapi.config.api.SpeedrunConfig;
import org.mcsr.speedrunapi.config.api.SpeedrunConfigStorage;
import org.mcsr.speedrunapi.config.exceptions.SpeedrunConfigAPIException;

import java.lang.reflect.Field;

public class FloatOption extends FractionalNumberOption<Float> {

    public FloatOption(SpeedrunConfig config, SpeedrunConfigStorage configStorage, Field option) {
        super(config, configStorage, option);
    }

    @Override
    public @NotNull Float get() {
        try {
            return this.option.getFloat(this.configStorage);
        } catch (IllegalAccessException e) {
            throw new SpeedrunConfigAPIException(e);
        }
    }

    @Override
    public void set(@NotNull Float value) {
        double min = this.getMin();
        double max = this.getMax();
        double intervals = this.getIntervals();

        if (this.bounds.enforce().enforceMin()) {
            value = (float) Math.max(value, min);
        }
        if (this.bounds.enforce().enforceMax()) {
            value = (float) Math.min(value, max);
        }

        if (intervals != 0) {
            double remainder = (value - min) % intervals;
            value = (float) (value - remainder + (remainder * 2.0 >= intervals ? intervals : 0.0));
        }

        try {
            if (this.setter != null) {
                this.setter.invoke(this.configStorage, value);
            }
            this.option.setFloat(this.configStorage, value);
        } catch (ReflectiveOperationException e) {
            throw new SpeedrunConfigAPIException(e);
        }
    }

    @Override
    public void fromJson(JsonElement jsonElement) {
        this.set(jsonElement.getAsFloat());
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(this.get());
    }

    @Override
    public void setDouble(double value) {
        this.set((float) value);
    }
}
