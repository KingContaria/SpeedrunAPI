package org.mcsr.speedrunapi.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mcsr.speedrunapi.config.api.annotations.Config;
import org.mcsr.speedrunapi.config.screen.widgets.option.StringOptionTextFieldWidget;

import java.lang.reflect.Field;

public class StringOption extends Option<String> {

    @Nullable
    private final Config.Strings.MaxChars maxLength;

    public StringOption(Object config, Field option) {
        super(config, option);

        this.maxLength = option.getAnnotation(Config.Strings.MaxChars.class);
        if (this.getMaxLength() <= 0) {
            throw new RuntimeException("Max String length cannot be 0 or less!");
        }
    }

    @Override
    public @NotNull String get() {
        try {
            return (String) this.option.get(this.config);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void set(@NotNull String value) {
        try {
            if (value.length() > this.getMaxLength()) {
                value = value.substring(0, this.getMaxLength() - 1);
            }
            this.option.set(this.config, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void fromJson(JsonElement jsonElement) {
        this.set(jsonElement.getAsString());
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(this.get());
    }

    @Override
    public AbstractButtonWidget createWidget() {
        return new StringOptionTextFieldWidget(this, 0, 0);
    }

    public int getMaxLength() {
        return this.maxLength != null ? this.maxLength.value() : 100;
    }
}
