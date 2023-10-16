package org.mcsr.speedrunapi.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.mcsr.speedrunapi.config.api.SpeedrunConfig;
import org.mcsr.speedrunapi.config.api.SpeedrunConfigStorage;
import org.mcsr.speedrunapi.config.exceptions.SpeedrunConfigAPIException;

import java.lang.reflect.Field;

public class IntegerOption extends WholeNumberOption<Integer> {

    public IntegerOption(SpeedrunConfig config, SpeedrunConfigStorage configStorage, Field option) {
        super(config, configStorage, option);
    }

    @Override
    public @NotNull Integer get() {
        try {
            return this.option.getInt(this.configStorage);
        } catch (IllegalAccessException e) {
            throw new SpeedrunConfigAPIException(e);
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
            }
            this.option.setInt(this.configStorage, value);
        } catch (ReflectiveOperationException e) {
            throw new SpeedrunConfigAPIException(e);
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
