package org.mcsr.speedrunapi.config.api;

import com.google.gson.JsonElement;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SpeedrunOption<T> {

    String getID();

    @Nullable String getCategory();

    void setCategory(@Nullable String category);

    String getModID();

    default @NotNull Text getName() {
        return new TranslatableText("speedrunapi.config." + this.getModID() + ".option." + this.getID());
    }

    default @Nullable Text getDescription() {
        return new TranslatableText("speedrunapi.config." + this.getModID() + ".option." + this.getID() + ".description");
    }

    T get();

    void set(T value);

    void setUnsafely(Object value);

    void fromJson(JsonElement jsonElement);

    JsonElement toJson();

    boolean hasWidget();

    AbstractButtonWidget createWidget();
}
