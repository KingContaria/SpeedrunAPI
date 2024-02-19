package org.mcsr.speedrunapi.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.NotNull;
import org.mcsr.speedrunapi.config.api.SpeedrunConfig;
import org.mcsr.speedrunapi.config.api.SpeedrunConfigStorage;
import org.mcsr.speedrunapi.config.api.option.EnumTextProvider;
import org.mcsr.speedrunapi.config.exceptions.SpeedrunConfigAPIException;
import org.mcsr.speedrunapi.config.screen.widgets.option.EnumOptionButtonWidget;

import java.lang.reflect.Field;

@SuppressWarnings("rawtypes")
public class EnumOption extends FieldBasedOption<Enum> {

    public EnumOption(SpeedrunConfig config, SpeedrunConfigStorage configStorage, Field option, String... idPrefix) {
        super(config, configStorage, option, idPrefix);
    }

    @Override
    public @NotNull Enum get() {
        try {
            if (this.getter != null) {
                return (Enum) this.getter.invoke(this.configStorage);
            }
            return (Enum) this.option.get(this.configStorage);
        } catch (ReflectiveOperationException e) {
            throw new SpeedrunConfigAPIException(e);
        }
    }

    @Override
    public void set(@NotNull Enum value) {
        try {
            if (this.setter != null) {
                this.setter.invoke(this.configStorage, value);
                return;
            }
            this.option.set(this.configStorage, value);
        } catch (ReflectiveOperationException e) {
            throw new SpeedrunConfigAPIException(e);
        }
    }

    @Override
    public void fromJson(JsonElement jsonElement) {
        String jsonString = jsonElement.getAsString();
        for (Enum enumConstant : (Enum[]) this.option.getType().getEnumConstants()) {
            if (enumConstant.name().equals(jsonString)) {
                this.set(enumConstant);
                break;
            }
        }
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(this.get().name());
    }

    @Override
    public boolean hasWidget() {
        return true;
    }

    @Override
    public @NotNull AbstractButtonWidget createWidget() {
        return new EnumOptionButtonWidget(this, 0, 0);
    }

    @Override
    public @NotNull Text getText() {
        Enum value = this.get();
        if (value instanceof EnumTextProvider) {
            return ((EnumTextProvider) value).toText();
        }
        return new TranslatableText("speedrunapi.config." + this.getModID() + ".option." + this.getID() + ".value." + value.name());
    }
}
