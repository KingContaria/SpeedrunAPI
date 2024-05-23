package org.mcsr.speedrunapi.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.mcsr.speedrunapi.config.api.SpeedrunConfig;
import org.mcsr.speedrunapi.config.api.SpeedrunConfigStorage;
import org.mcsr.speedrunapi.config.exceptions.ReflectionConfigException;

import java.lang.reflect.Field;

@ApiStatus.Internal
public class LongOption extends WholeNumberOption<Long> {

    public LongOption(SpeedrunConfig config, SpeedrunConfigStorage configStorage, Field option, String... idPrefix) {
        super(config, configStorage, option, idPrefix);
    }

    @Override
    public @NotNull Long get() {
        try {
            if (this.getter != null) {
                return (Long) this.getter.invoke(this.configStorage);
            }
            return this.option.getLong(this.configStorage);
        } catch (ReflectiveOperationException e) {
            throw new ReflectionConfigException("Failed to get value for option " + this.getID() + " in " + this.getModID() + "config.", e);
        }
    }

    @Override
    public void set(@NotNull Long value) {
        long min = this.getMin();
        long max = this.getMax();
        long intervals = this.getIntervals();

        if (this.bounds.enforce().enforceMin()) {
            value = Math.max(value, min);
        }
        if (this.bounds.enforce().enforceMax()) {
            value = Math.min(value, max);
        }

        if (intervals != 0) {
            long remainder = (value - min) % intervals;
            value = value - remainder + (remainder * 2 >= intervals ? intervals : 0);
        }

        try {
            if (this.setter != null) {
                this.setter.invoke(this.configStorage, value);
                return;
            }
            this.option.setLong(this.configStorage, Math.max(this.getMin(), Math.min(this.getMax(), value)));
        } catch (ReflectiveOperationException e) {
            throw new ReflectionConfigException("Failed to set value for option " + this.getID() + " in " + this.getModID() + "config.", e);
        }
    }

    @Override
    public void fromJson(JsonElement jsonElement) {
        this.set(jsonElement.getAsLong());
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(this.get());
    }

    @Override
    public void setLong(long value) {
        this.set(value);
    }
}
