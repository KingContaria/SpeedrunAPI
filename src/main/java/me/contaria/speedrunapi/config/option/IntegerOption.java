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
public class IntegerOption extends WholeNumberOption<Integer> {

    public IntegerOption(SpeedrunConfig config, SpeedrunConfigStorage configStorage, Field option, String... idPrefix) {
        super(config, configStorage, option, idPrefix);
    }

    @Override
    public @NotNull Integer get() {
        try {
            if (this.getter != null) {
                return (Integer) this.getter.invoke(this.configStorage);
            }
            return this.option.getInt(this.configStorage);
        } catch (ReflectiveOperationException e) {
            throw new ReflectionConfigException("Failed to get value for option " + this.getID() + " in " + this.getModID() + "config.", e);
        }
    }

    @Override
    public void set(@NotNull Integer value) {
        long min = this.getMin();
        long max = this.getMax();
        long intervals = this.getIntervals();

        if (this.bounds.enforce().enforceMin()) {
            value = (int) Math.max(value, min);
        }
        if (this.bounds.enforce().enforceMax()) {
            value = (int) Math.min(value, max);
        }

        if (intervals != 0) {
            long remainder = (value - min) % intervals;
            value = (int) (value - remainder + (remainder * 2 >= intervals ? intervals : 0));
        }

        try {
            if (this.setter != null) {
                this.setter.invoke(this.configStorage, value);
                return;
            }
            this.option.setInt(this.configStorage, value);
        } catch (ReflectiveOperationException e) {
            throw new ReflectionConfigException("Failed to set value for option " + this.getID() + " in " + this.getModID() + "config.", e);
        }
    }

    @Override
    public void fromJson(JsonElement jsonElement) {
        this.set(jsonElement.getAsInt());
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(this.get());
    }

    @Override
    public void setLong(long value) {
        this.set((int) value);
    }
}
