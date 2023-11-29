package org.mcsr.speedrunapi.config.option;

import com.google.gson.JsonElement;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import org.jetbrains.annotations.Nullable;
import org.mcsr.speedrunapi.config.api.SpeedrunConfig;
import org.mcsr.speedrunapi.config.api.SpeedrunConfigStorage;

import java.lang.reflect.Field;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CustomOption<T> extends BaseOption<T> {

    private final Supplier<T> getFunction;
    private final Consumer<T> setFunction;
    private final Consumer<JsonElement> fromJsonFunction;
    private final Supplier<JsonElement> toJsonFunction;
    @Nullable
    private final Supplier<AbstractButtonWidget> createWidgetFunction;

    public CustomOption(SpeedrunConfig config, SpeedrunConfigStorage configStorage, Field option, String[] idPrefix, Supplier<T> getFunction, Consumer<T> setFunction, Consumer<JsonElement> fromJsonFunction, Supplier<JsonElement> toJsonFunction, @Nullable Supplier<AbstractButtonWidget> createWidgetFunction) {
        super(config, configStorage, option, idPrefix);
        this.getFunction = getFunction;
        this.setFunction = setFunction;
        this.fromJsonFunction = fromJsonFunction;
        this.toJsonFunction = toJsonFunction;
        this.createWidgetFunction = createWidgetFunction;
    }

    @Override
    public T get() {
        return this.getFunction.get();
    }

    @Override
    public void set(T value) {
        this.setFunction.accept(value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setUnsafely(Object value) {
        this.set((T) value);
    }

    @Override
    public void fromJson(JsonElement jsonElement) {
        this.fromJsonFunction.accept(jsonElement);
    }

    @Override
    public JsonElement toJson() {
        return this.toJsonFunction.get();
    }

    @Override
    public boolean hasWidget() {
        return this.createWidgetFunction != null;
    }

    @Override
    public AbstractButtonWidget createWidget() {
        if (this.createWidgetFunction == null) {
            throw new UnsupportedOperationException("No widget supplier given for " + this.getID() + " in " + this.getModID() + "config.");
        }
        return this.createWidgetFunction.get();
    }
}
