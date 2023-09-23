package org.mcsr.speedrunapi.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import org.jetbrains.annotations.NotNull;
import org.mcsr.speedrunapi.config.screen.widgets.option.BooleanOptionButtonWidget;

import java.lang.reflect.Field;

public class BooleanOption extends Option<Boolean> {

    public BooleanOption(Object config, Field option) {
        super(config, option);
    }

    @Override
    public @NotNull Boolean get() {
        try {
            return this.option.getBoolean(this.config);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void set(@NotNull Boolean value) {
        try {
            this.option.setBoolean(this.config, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void fromJson(JsonElement jsonElement) {
        this.set(jsonElement.getAsBoolean());
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(this.get());
    }

    @Override
    public AbstractButtonWidget createWidget() {
        return new BooleanOptionButtonWidget(this, 0, 0);
    }
}
