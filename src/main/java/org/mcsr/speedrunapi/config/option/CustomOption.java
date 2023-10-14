package org.mcsr.speedrunapi.config.option;

import com.google.gson.JsonElement;
import net.minecraft.client.gui.widget.AbstractButtonWidget;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class CustomOption<T> extends Option<T> {

    private final String id;
    private final String category;
    private final String modID;
    private final Supplier<T> getFunction;
    private final Consumer<T> setFunction;
    private final Consumer<JsonElement> fromJsonFunction;
    private final Supplier<JsonElement> toJsonFunction;
    private final Supplier<AbstractButtonWidget> createWidgetFunction;

    public CustomOption(String id, String category, String modID, Supplier<T> getFunction, Consumer<T> setFunction, Consumer<JsonElement> fromJsonFunction, Supplier<JsonElement> toJsonFunction, Supplier<AbstractButtonWidget> createWidgetFunction) {
        this.id = id;
        this.category = category;
        this.modID = modID;
        this.getFunction = getFunction;
        this.setFunction = setFunction;
        this.fromJsonFunction = fromJsonFunction;
        this.toJsonFunction = toJsonFunction;
        this.createWidgetFunction = createWidgetFunction;
    }

    @Override
    public String getID() {
        return this.id;
    }

    @Override
    public String getCategory() {
        return this.category;
    }

    @Override
    public String getModID() {
        return this.modID;
    }

    @Override
    public T get() {
        return this.getFunction.get();
    }

    @Override
    public void set(T value) {
        this.setFunction.accept(value);
    }

    @Override
    public void setUnsafely(Object value) {
        this.setFunction.accept((T) value);
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
    public AbstractButtonWidget createWidget() {
        return this.createWidgetFunction.get();
    }
}
