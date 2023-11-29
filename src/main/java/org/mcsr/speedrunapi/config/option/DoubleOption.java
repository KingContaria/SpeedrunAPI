package org.mcsr.speedrunapi.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.mcsr.speedrunapi.config.api.SpeedrunConfig;
import org.mcsr.speedrunapi.config.api.SpeedrunConfigStorage;
import org.mcsr.speedrunapi.config.exceptions.SpeedrunConfigAPIException;

import java.lang.reflect.Field;

public class DoubleOption extends FractionalNumberOption<Double> {

    public DoubleOption(SpeedrunConfig config, SpeedrunConfigStorage configStorage, Field option, String... idPrefix) {
        super(config, configStorage, option, idPrefix);
    }

    @Override
    public @NotNull Double get() {
        try {
            if (this.getter != null) {
                return (Double) this.getter.invoke(this.configStorage);
            }
            return this.option.getDouble(this.configStorage);
        } catch (ReflectiveOperationException e) {
            throw new SpeedrunConfigAPIException(e);
        }
    }

    @Override
    public void set(@NotNull Double value) {
        double min = this.getMin();
        double max = this.getMax();
        double intervals = this.getIntervals();

        if (this.bounds.enforce().enforceMin()) {
            value = Math.max(value, min);
        }
        if (this.bounds.enforce().enforceMax()) {
            value = Math.min(value, max);
        }

        if (intervals != 0) {
            double remainder = (value - min) % intervals;
            value = value - remainder + (remainder * 2.0 >= intervals ? intervals : 0.0);
        }

        try {
            if (this.setter != null) {
                this.setter.invoke(this.configStorage, value);
                return;
            }
            this.option.setDouble(this.configStorage, value);
        } catch (ReflectiveOperationException e) {
            throw new SpeedrunConfigAPIException(e);
        }
    }

    @Override
    public void fromJson(JsonElement jsonElement) {
        this.set(jsonElement.getAsDouble());
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(this.get());
    }

    @Override
    public void setDouble(double value) {
        this.set(value);
    }
}
