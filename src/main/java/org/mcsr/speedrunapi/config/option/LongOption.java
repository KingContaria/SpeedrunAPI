package org.mcsr.speedrunapi.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class LongOption extends WholeNumberOption<Long> {

    public LongOption(Object config, Field option) {
        super(config, option);
    }

    @Override
    public @NotNull Long get() {
        try {
            return this.option.getLong(this.config);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void set(@NotNull Long value) {
        try {
            this.option.setLong(this.config, MathHelper.clamp(value, this.getMin(), this.getMax()));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
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
