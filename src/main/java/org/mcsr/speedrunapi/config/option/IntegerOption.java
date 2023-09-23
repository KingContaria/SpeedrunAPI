package org.mcsr.speedrunapi.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class IntegerOption extends WholeNumberOption<Integer> {

    public IntegerOption(Object config, Field option) {
        super(config, option);
    }

    @Override
    public @NotNull Integer get() {
        try {
            return this.option.getInt(this.config);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void set(@NotNull Integer value) {
        try {
            this.option.setInt(this.config, MathHelper.clamp(value, (int) this.getMin(), (int) this.getMax()));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
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
