package me.contaria.speedrunapi.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.contaria.speedrunapi.config.api.SpeedrunConfig;
import me.contaria.speedrunapi.config.api.SpeedrunConfigStorage;
import me.contaria.speedrunapi.config.api.option.EnumTextProvider;
import me.contaria.speedrunapi.config.exceptions.ReflectionConfigException;
import me.contaria.speedrunapi.config.screen.widgets.option.EnumOptionButtonWidget;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.resource.language.I18n;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

@SuppressWarnings("rawtypes")
@ApiStatus.Internal
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
            throw new ReflectionConfigException("Failed to get value for option " + this.getID() + " in " + this.getModID() + "config.", e);
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
            throw new ReflectionConfigException("Failed to set value for option " + this.getID() + " in " + this.getModID() + "config.", e);
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
    public @NotNull AbstractButtonWidget createWidget() {
        return new EnumOptionButtonWidget(this, 0, 0);
    }

    @Override
    public @NotNull String getText() {
        Enum value = this.get();
        if (value instanceof EnumTextProvider) {
            return ((EnumTextProvider) value).toText();
        }
        return I18n.translate("speedrunapi.config." + this.getModID() + ".option." + this.getID() + ".value." + value.name());
    }
}
