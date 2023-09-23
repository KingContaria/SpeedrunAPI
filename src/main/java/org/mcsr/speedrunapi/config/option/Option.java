package org.mcsr.speedrunapi.config.option;

import com.google.gson.JsonElement;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.NotNull;
import org.mcsr.speedrunapi.config.api.annotations.Config;
import org.mcsr.speedrunapi.config.api.annotations.SpeedrunConfig;

import java.lang.reflect.Field;

public abstract class Option<T> {

    protected final Object config;
    protected final Field option;

    public Option(Object config, Field option) {
        this.config = config;
        this.option = option;
        this.option.setAccessible(true);
    }

    public @NotNull abstract T get();

    public abstract void set(@NotNull T value);

    public abstract void fromJson(JsonElement jsonElement);

    public abstract JsonElement toJson();

    public abstract AbstractButtonWidget createWidget();

    public String getID() {
        return this.option.getName();
    }

    public Text getName() {
        Config.Name name = this.option.getAnnotation(Config.Name.class);
        if (name != null) {
            return name.literal() ? new LiteralText(name.value()) : new TranslatableText(name.value());
        }
        if (this.option.isAnnotationPresent(Config.Name.Auto.class)) {
            return new TranslatableText("speedrunapi.config." + this.config.getClass().getAnnotation(SpeedrunConfig.class).modID() + ".option." + this.getID());
        }
        return new LiteralText(this.getID());
    }

    public Text getDescription() {
        Config.Description description = this.option.getAnnotation(Config.Description.class);
        if (description != null) {
            return description.literal() ? new LiteralText(description.value()) : new TranslatableText(description.value());
        }
        if (this.option.isAnnotationPresent(Config.Description.Auto.class)) {
            return new TranslatableText("speedrunapi.config." + this.config.getClass().getAnnotation(SpeedrunConfig.class).modID() + ".option." + this.getID() + ".description");
        }
        return null;
    }
}
