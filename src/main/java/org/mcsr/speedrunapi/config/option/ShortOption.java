package org.mcsr.speedrunapi.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.mcsr.speedrunapi.config.api.SpeedrunConfig;
import org.mcsr.speedrunapi.config.api.SpeedrunConfigStorage;
import org.mcsr.speedrunapi.config.exceptions.SpeedrunConfigAPIException;

import java.lang.reflect.Field;

public class ShortOption extends WholeNumberOption<Short> {

    public ShortOption(SpeedrunConfig config, SpeedrunConfigStorage configStorage, Field option) {
        super(config, configStorage, option);
    }

    @Override
    public @NotNull Short get() {
        try {
            return this.option.getShort(this.configStorage);
        } catch (IllegalAccessException e) {
            throw new SpeedrunConfigAPIException(e);
        }
    }

    @Override
    public void set(@NotNull Short value) {
        long min = this.getMin();
        long max = this.getMax();
        long intervals = this.getIntervals();

        if (this.bounds.enforce().enforceMin()) {
            value = (short) Math.max(value, min);
        }
        if (this.bounds.enforce().enforceMax()) {
            value = (short) Math.min(value, max);
        }

        if (intervals != 0) {
            long remainder = (value - min) % intervals;
            value = (short) (value - remainder + (remainder * 2 >= intervals ? intervals : 0));
        }

        try {
            if (this.setter != null) {
                this.setter.invoke(this.configStorage, value);
            }
            this.option.setShort(this.configStorage, (short) MathHelper.clamp(value, this.getMin(), this.getMax()));
        } catch (ReflectiveOperationException e) {
            throw new SpeedrunConfigAPIException(e);
        }
    }

    @Override
    public void fromJson(JsonElement jsonElement) {
        this.set(jsonElement.getAsShort());
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(this.get());
    }

    @Override
    public void setLong(long value) {
        this.set((short) value);
    }
}
