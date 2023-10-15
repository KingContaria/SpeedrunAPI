package org.mcsr.speedrunapi.config.option;

import com.google.gson.JsonElement;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Option<T> {

    @Nullable
    protected String category;

    public abstract String getID();

    public @Nullable String getCategory() {
        return this.category;
    }

    public void setCategory(@Nullable String category) {
        this.category = category;
    }

    public abstract String getModID();

    public @NotNull Text getName() {
        return new TranslatableText("speedrunapi.config." + this.getModID() + ".option." + this.getID());
    }

    public @Nullable Text getDescription() {
        return new TranslatableText("speedrunapi.config." + this.getModID() + ".option." + this.getID() + ".description");
    }

    public abstract T get();

    public abstract void set(T value);

    public abstract void setUnsafely(Object value);

    public abstract void fromJson(JsonElement jsonElement);

    public abstract JsonElement toJson();

    public abstract AbstractButtonWidget createWidget();
}
