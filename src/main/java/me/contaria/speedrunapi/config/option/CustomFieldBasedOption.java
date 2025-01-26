package me.contaria.speedrunapi.config.option;

import com.google.gson.JsonElement;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import me.contaria.speedrunapi.config.SpeedrunConfigAPI;
import me.contaria.speedrunapi.config.api.SpeedrunConfig;
import me.contaria.speedrunapi.config.api.SpeedrunConfigStorage;
import me.contaria.speedrunapi.config.exceptions.ReflectionConfigException;

import java.lang.reflect.Field;

@ApiStatus.Internal
public class CustomFieldBasedOption<T> extends FieldBasedOption<T> {
    private final SpeedrunConfigAPI.CustomOption.Getter<T> getFunction;
    private final SpeedrunConfigAPI.CustomOption.Setter<T> setFunction;
    private final SpeedrunConfigAPI.CustomOption.Deserializer<T> fromJsonFunction;
    private final SpeedrunConfigAPI.CustomOption.Serializer<T> toJsonFunction;
    @Nullable
    private final SpeedrunConfigAPI.CustomOption.WidgetProvider<T> createWidgetFunction;

    public CustomFieldBasedOption(SpeedrunConfig config, SpeedrunConfigStorage configStorage, Field option, String[] idPrefix, SpeedrunConfigAPI.CustomOption.Getter<T> getter, SpeedrunConfigAPI.CustomOption.Setter<T> setter, SpeedrunConfigAPI.CustomOption.Deserializer<T> fromJson, SpeedrunConfigAPI.CustomOption.Serializer<T> toJson, @Nullable SpeedrunConfigAPI.CustomOption.WidgetProvider<T> createWidget, boolean hasDefault) {
        super(config, configStorage, option, hasDefault, idPrefix);
        this.getFunction = getter;
        this.setFunction = setter;
        this.fromJsonFunction = fromJson;
        this.toJsonFunction = toJson;
        this.createWidgetFunction = createWidget;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get() {
        try {
            if (this.getter != null) {
                return (T) this.getter.invoke(this.configStorage);
            }
            return this.getFunction.get(this, this.config, this.configStorage, this.option);
        } catch (ReflectiveOperationException e) {
            throw new ReflectionConfigException("Failed to get value for option " + this.getID() + " in " + this.getModID() + "config.", e);
        }
    }

    @Override
    public void set(T value) {
        try {
            if (this.setter != null) {
                this.setter.invoke(this.configStorage, value);
                return;
            }
            this.setFunction.set(this, this.config, this.configStorage, this.option, value);
        } catch (ReflectiveOperationException e) {
            throw new ReflectionConfigException("Failed to set value for option " + this.getID() + " in " + this.getModID() + "config.", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setUnsafely(Object value) {
        this.set((T) value);
    }

    @Override
    public void fromJson(JsonElement jsonElement) {
        this.fromJsonFunction.fromJson(this, this.config, this.configStorage, this.option, jsonElement);
    }

    @Override
    public JsonElement toJson() {
        return this.toJsonFunction.toJson(this, this.config, this.configStorage, this.option);
    }

    @Override
    public boolean hasWidget() {
        return super.hasWidget() && this.createWidgetFunction != null;
    }

    @Override
    public @NotNull AbstractButtonWidget createWidget() {
        if (this.createWidgetFunction == null) {
            throw new UnsupportedOperationException("No widget supplier given for " + this.getID() + " in " + this.getModID() + "config.");
        }
        return this.createWidgetFunction.createWidget(this, this.config, this.configStorage, this.option);
    }
}
