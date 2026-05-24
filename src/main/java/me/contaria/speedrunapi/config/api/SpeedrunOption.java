package me.contaria.speedrunapi.config.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import me.contaria.speedrunapi.util.TextUtil;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SpeedrunOption<T> {

    /**
     * @return Returns this options id.
     */
    String getID();

    /**
     * @return Returns this options category on the config screen.
     */
    @Nullable
    String getCategory();

    /**
     * Sets this options category.
     *
     * @apiNote This method is reserved for use in {@link SpeedrunConfigStorage#init}, using it anywhere else is not supported!
     * @see SpeedrunOption#getCategory
     */
    void setCategory(@Nullable String category);

    /**
     * @return Returns the mod id corresponding to this option.
     */
    String getModID();

    /**
     * @return Returns the name of this option.
     */
    default @NotNull Component getName() {
        return TextUtil.translatable("speedrunapi.config." + this.getModID() + ".option." + this.getID());
    }

    /**
     * @return Returns the description of this option.
     */
    default @Nullable Component getDescription() {
        Language language = Language.getInstance();
        String description = "speedrunapi.config." + this.getModID() + ".option." + this.getID() + ".description";
        if (language.has(description)) {
            return TextUtil.translatable(description);
        }
        return null;
    }

    /**
     * @return Returns the value of this option as a {@link Component}.
     * @see SpeedrunOption#getDefaultText
     */
    default @NotNull Component getText() {
        Language language = Language.getInstance();
        String value = "speedrunapi.config." + this.getModID() + ".option." + this.getID() + ".value";
        String valueSpecified = value + "." + this.get();
        if (language.has(valueSpecified)) {
            return TextUtil.translatable(valueSpecified);
        }
        if (language.has(value)) {
            return TextUtil.translatable(value, this.get());
        }
        return this.getDefaultText();
    }

    /**
     * @return Returns the default {@link Component} returned by {@link SpeedrunOption#getText}.
     * @see SpeedrunOption#getText
     */
    default @NotNull Component getDefaultText() {
        return TextUtil.literal(this.getString());
    }

    /**
     * @return Returns the value as a {@link String}.
     */
    default @NotNull String getString() {
        return this.get().toString();
    }

    /**
     * @return Returns the value of this option.
     */
    T get();

    /**
     * Sets the value of this option.
     */
    void set(T value);

    /**
     * Sets the value of this option with an unsafe cast, use with caution!
     */
    void setUnsafely(Object value) throws ClassCastException;

    /**
     * @return Returns true if the option has a default value.
     * @see SpeedrunOption#getDefault
     */
    default boolean hasDefault() {
        return false;
    }

    /**
     * @return Returns the default value of this option.
     */
    default T getDefault() {
        throw new UnsupportedOperationException("SpeedrunOption does not have a default value!");
    }

    /**
     * Sets the value of this option from a {@link JsonElement}.
     */
    void fromJson(JsonElement jsonElement);

    /**
     * @return Returns a {@link JsonElement} representing the value of this option.
     * @apiNote If this returns {@code null}, the option is not saved to the config file. To save a null value return {@link JsonNull#INSTANCE}.
     */
    JsonElement toJson();

    /**
     * @return Returns true if the option has a widget and should be added to the config screen.
     * @see SpeedrunOption#createWidget
     */
    boolean hasWidget();

    /**
     * Creates an {@link AbstractWidget} to configure this option on the mods config screen.
     * <p>
     * X and y will be set by the config screen and should be left as 0, 0.
     * Width and height should be set to 150, 20 by default to fit the option list widget, unless you know what you're doing.
     *
     * @return Returns a new {@link AbstractWidget} to be added to the config screen.
     */
    @NotNull
    AbstractWidget createWidget();
}
