package org.mcsr.speedrunapi.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.mcsr.speedrunapi.config.api.SpeedrunConfig;
import org.mcsr.speedrunapi.config.exceptions.SpeedrunConfigAPIException;

import java.lang.reflect.Field;

public class LongOption extends WholeNumberOption<Long> {

    public LongOption(SpeedrunConfig config, Field option) {
        super(config, option);
    }

    @Override
    public @NotNull Long get() {
        try {
            return this.option.getLong(this.config);
        } catch (IllegalAccessException e) {
            throw new SpeedrunConfigAPIException(e);
        }
    }

    @Override
    public void set(@NotNull Long value) {
        long min = this.getMin();
        long max = this.getMax();
        long intervals = this.getIntervals();

        value = MathHelper.clamp(value, min, max);

        long remainder = (value - min) % intervals;
        value = value - remainder + (remainder * 2 >= intervals ? intervals : 0);

        try {
            if (this.setter != null) {
                this.setter.invoke(this.config, value);
            }
            this.option.setLong(this.config, MathHelper.clamp(value, this.getMin(), this.getMax()));
        } catch (ReflectiveOperationException e) {
            throw new SpeedrunConfigAPIException(e);
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
