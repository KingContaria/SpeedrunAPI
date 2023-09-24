package org.mcsr.speedrunapi.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class FloatOption extends FractionalNumberOption<Float> {

    public FloatOption(Object config, Field option) {
        super(config, option);
    }

    @Override
    public @NotNull Float get() {
        try {
            return this.option.getFloat(this.config);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void set(@NotNull Float value) {
        double min = this.getMin();
        double max = this.getMax();
        double intervals = this.getIntervals();

        value = (float) MathHelper.clamp(value, min, max);

        double remainder = value % intervals;
        value = (float) (value - remainder + (remainder * 2.0 >= intervals ? intervals : 0.0));

        try {
            this.option.setFloat(this.config, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
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
