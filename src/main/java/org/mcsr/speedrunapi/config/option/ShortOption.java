package org.mcsr.speedrunapi.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class ShortOption extends WholeNumberOption<Short> {

    public ShortOption(Object config, Field option) {
        super(config, option);
    }

    @Override
    public @NotNull Short get() {
        try {
            return this.option.getShort(this.config);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void set(@NotNull Short value) {
        long min = this.getMin();
        long max = this.getMax();
        long intervals = this.getIntervals();

        value = (short) MathHelper.clamp(value, min, max);

        long remainder = (value - min) % intervals;
        value = (short) (value - remainder + (remainder * 2 >= intervals ? intervals : 0));

        try {
            this.option.setShort(this.config, (short) MathHelper.clamp(value, this.getMin(), this.getMax()));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
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
