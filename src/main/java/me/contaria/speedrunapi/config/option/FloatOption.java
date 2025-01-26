package me.contaria.speedrunapi.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.contaria.speedrunapi.config.api.SpeedrunConfig;
import me.contaria.speedrunapi.config.api.SpeedrunConfigStorage;
import me.contaria.speedrunapi.config.exceptions.ReflectionConfigException;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

@ApiStatus.Internal
public class FloatOption extends FractionalNumberOption<Float> {

    public FloatOption(SpeedrunConfig config, SpeedrunConfigStorage configStorage, Field option, String... idPrefix) {
        super(config, configStorage, option, idPrefix);
    }

    @Override
    public @NotNull Float get() {
        try {
            if (this.getter != null) {
                return (Float) this.getter.invoke(this.configStorage);
            }
            return this.option.getFloat(this.configStorage);
        } catch (ReflectiveOperationException e) {
            throw new ReflectionConfigException("Failed to get value for option " + this.getID() + " in " + this.getModID() + "config.", e);
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
                return;
            }
            this.option.setFloat(this.configStorage, value);
        } catch (ReflectiveOperationException e) {
            throw new ReflectionConfigException("Failed to set value for option " + this.getID() + " in " + this.getModID() + "config.", e);
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
