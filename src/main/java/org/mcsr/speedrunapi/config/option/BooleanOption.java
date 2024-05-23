package org.mcsr.speedrunapi.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.mcsr.speedrunapi.config.api.SpeedrunConfig;
import org.mcsr.speedrunapi.config.api.SpeedrunConfigStorage;
import org.mcsr.speedrunapi.config.exceptions.SpeedrunConfigAPIException;
import org.mcsr.speedrunapi.config.screen.widgets.option.BooleanOptionButtonWidget;

import java.lang.reflect.Field;

@ApiStatus.Internal
public class BooleanOption extends FieldBasedOption<Boolean> {

    public BooleanOption(SpeedrunConfig config, SpeedrunConfigStorage configStorage, Field option, String... idPrefix) {
        super(config, configStorage, option, idPrefix);
    }

    @Override
    public @NotNull Text getDefaultText() {
        return ScreenTexts.getToggleText(this.get());
    }

    @Override
    public @NotNull Boolean get() {
        try {
            if (this.getter != null) {
                return (Boolean) this.getter.invoke(this.configStorage);
            }
            return this.option.getBoolean(this.configStorage);
        } catch (ReflectiveOperationException e) {
            throw new SpeedrunConfigAPIException(e);
        }
    }

    @Override
    public void set(@NotNull Boolean value) {
        try {
            if (this.setter != null) {
                this.setter.invoke(this.configStorage, value);
                return;
            }
            this.option.setBoolean(this.configStorage, value);
        } catch (ReflectiveOperationException e) {
            throw new SpeedrunConfigAPIException(e);
        }
    }

    @Override
    public void fromJson(JsonElement jsonElement) {
        this.set(jsonElement.getAsBoolean());
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(this.get());
    }

    @Override
    public boolean hasWidget() {
        return true;
    }

    @Override
    public @NotNull AbstractButtonWidget createWidget() {
        return new BooleanOptionButtonWidget(this, 0, 0);
    }
}
