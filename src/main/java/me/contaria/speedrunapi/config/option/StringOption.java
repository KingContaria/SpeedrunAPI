package me.contaria.speedrunapi.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.contaria.speedrunapi.config.api.SpeedrunConfig;
import me.contaria.speedrunapi.config.api.SpeedrunConfigStorage;
import me.contaria.speedrunapi.config.api.annotations.Config;
import me.contaria.speedrunapi.config.exceptions.InvalidConfigException;
import me.contaria.speedrunapi.config.exceptions.ReflectionConfigException;
import me.contaria.speedrunapi.config.screen.widgets.option.StringOptionTextFieldWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

@ApiStatus.Internal
public class StringOption extends FieldBasedOption<String> {
    @Nullable
    private final Integer maxChars;

    public StringOption(SpeedrunConfig config, SpeedrunConfigStorage configStorage, Field option, String... idPrefix) {
        super(config, configStorage, option, idPrefix);

        Config.Strings.MaxChars maxChars = option.getAnnotation(Config.Strings.MaxChars.class);
        if (maxChars != null) {
            this.maxChars = maxChars.value();
        } else {
            this.maxChars = null;
        }

        if (this.getMaxLength() <= 0) {
            throw new InvalidConfigException("Max String length cannot be 0 or less!");
        }
    }

    @Override
    public @NotNull String get() {
        try {
            if (this.getter != null) {
                return (String) this.getter.invoke(this.configStorage);
            }
            return (String) this.option.get(this.configStorage);
        } catch (ReflectiveOperationException e) {
            throw new ReflectionConfigException("Failed to get value for option " + this.getID() + " in " + this.getModID() + "config.", e);
        }
    }

    @Override
    public void set(@NotNull String value) {
        try {
            if (value.length() > this.getMaxLength()) {
                value = value.substring(0, this.getMaxLength() - 1);
            }
            if (this.setter != null) {
                this.setter.invoke(this.configStorage, value);
                return;
            }
            this.option.set(this.configStorage, value);
        } catch (ReflectiveOperationException e) {
            throw new ReflectionConfigException("Failed to set value for option " + this.getID() + " in " + this.getModID() + "config.", e);
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
    public @NotNull ClickableWidget createWidget() {
        return new StringOptionTextFieldWidget(this, 0, 0);
    }

    public int getMaxLength() {
        return this.maxChars != null ? this.maxChars : Integer.MAX_VALUE;
    }
}
