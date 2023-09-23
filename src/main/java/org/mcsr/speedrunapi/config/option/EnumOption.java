package org.mcsr.speedrunapi.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.mcsr.speedrunapi.config.api.EnumTextProvider;
import org.mcsr.speedrunapi.config.screen.widgets.option.EnumOptionButtonWidget;

import java.lang.reflect.Field;

@SuppressWarnings("rawtypes")
public class EnumOption extends Option<Enum> {

    public EnumOption(Object config, Field option) {
        super(config, option);
    }

    @Override
    public @NotNull Enum get() {
        try {
            return (Enum) this.option.get(this.config);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void set(@NotNull Enum value) {
        try {
            this.option.set(this.config, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void fromJson(JsonElement jsonElement) {
        String jsonString = jsonElement.getAsString();
        for (Object enumConstant : this.option.getType().getEnumConstants()) {
            if (enumConstant.toString().equals(jsonString)) {
                this.set((Enum) enumConstant);
                break;
            }
        }
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(this.get().toString());
    }

    @Override
    public AbstractButtonWidget createWidget() {
        return new EnumOptionButtonWidget(this, 0, 0);
    }

    public Text getText() {
        Enum value = this.get();
        if (value instanceof EnumTextProvider) {
            return ((EnumTextProvider) value).toText();
        }
        return new LiteralText(value.name());
    }
}
