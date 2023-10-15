package org.mcsr.speedrunapi.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.mcsr.speedrunapi.config.api.SpeedrunConfig;
import org.mcsr.speedrunapi.config.exceptions.SpeedrunConfigAPIException;

import java.lang.reflect.Field;

public class FloatOption extends FractionalNumberOption<Float> {

    public FloatOption(SpeedrunConfig config, Field option) {
        super(config, option);
    }

    @Override
    public @NotNull Float get() {
        try {
            return this.option.getFloat(this.config);
        } catch (IllegalAccessException e) {
            throw new SpeedrunConfigAPIException(e);
        }
    }

    @Override
    public void set(@NotNull Float value) {
        double min = this.getMin();
        double max = this.getMax();
        double intervals = this.getIntervals();

        if (this.bounds.enforce()) {
            value = (float) MathHelper.clamp(value, min, max);
        }

        if (intervals != 0) {
            double remainder = (value - min) % intervals;
            value = (float) (value - remainder + (remainder * 2.0 >= intervals ? intervals : 0.0));
        }

        try {
            if (this.setter != null) {
                this.setter.invoke(this.config, value);
            }
            this.option.setFloat(this.config, value);
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
