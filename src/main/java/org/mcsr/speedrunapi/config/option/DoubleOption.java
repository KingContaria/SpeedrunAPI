package org.mcsr.speedrunapi.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class DoubleOption extends FractionalNumberOption<Double> {

    public DoubleOption(Object config, Field option) {
        super(config, option);
    }

    @Override
    public @NotNull Double get() {
        try {
            return this.option.getDouble(this.config);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void set(@NotNull Double value) {
        try {
            this.option.setDouble(this.config, MathHelper.clamp(value, this.getMin(), this.getMax()));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
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
