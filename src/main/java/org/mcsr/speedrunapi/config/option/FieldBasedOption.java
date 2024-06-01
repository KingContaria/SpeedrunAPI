package org.mcsr.speedrunapi.config.option;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mcsr.speedrunapi.config.api.SpeedrunConfig;
import org.mcsr.speedrunapi.config.api.SpeedrunConfigStorage;
import org.mcsr.speedrunapi.config.api.SpeedrunOption;
import org.mcsr.speedrunapi.config.api.annotations.Config;
import org.mcsr.speedrunapi.config.exceptions.InvalidConfigException;
import org.mcsr.speedrunapi.config.exceptions.ReflectionConfigException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@ApiStatus.Internal
public abstract class FieldBasedOption<T> implements SpeedrunOption<T> {

    protected final SpeedrunConfig config;
    protected final SpeedrunConfigStorage configStorage;
    protected final Field option;

    private final String[] idPrefix;

    @Nullable
    private final String name;
    @Nullable
    private final String description;
    @Nullable
    protected String category;

    private final boolean hide;

    @Nullable
    protected final Method getter;
    @Nullable
    protected final Method setter;

    public FieldBasedOption(SpeedrunConfig config, SpeedrunConfigStorage configStorage, Field option, String... idPrefix) {
        this.config = config;
        this.configStorage = configStorage;
        this.option = option;
        this.option.setAccessible(true);
        this.idPrefix = idPrefix;

        Config.Name name = option.getAnnotation(Config.Name.class);
        if (name != null) {
            this.name = name.value();
        } else {
            this.name = null;
        }

        Config.Description description = option.getAnnotation(Config.Description.class);
        if (description != null) {
            this.description = description.value();
        } else {
            this.description = null;
        }

        Config.Category category = option.getAnnotation(Config.Category.class);
        if (category != null) {
            this.category = category.value();
        }

        this.hide = option.getAnnotation(Config.Hide.class) != null;

        Method getter = null;
        Method setter = null;
        Config.Access access = option.getAnnotation(Config.Access.class);
        if (access != null) {
            if (!access.getter().isEmpty()) {
                try {
                    getter = this.configStorage.getClass().getDeclaredMethod(access.getter());
                    if (!getter.getReturnType().equals(option.getType())) {
                        throw new InvalidConfigException("Provided getter method for \"" + this.getID() + "\" does not exist in " + this.getModID() + " config (" + this.configStorage.getClass().getName() + ").");
                    }
                    getter.setAccessible(true);
                } catch (NoSuchMethodException e) {
                    throw new InvalidConfigException("Provided getter method for \"" + this.getID() + "\" does not exist in " + this.getModID() + " config (" + this.configStorage.getClass().getName() + ").", e);
                }
            }
            if (!access.setter().isEmpty()) {
                try {
                    setter = this.configStorage.getClass().getDeclaredMethod(access.setter(), option.getType());
                    setter.setAccessible(true);
                } catch (NoSuchMethodException e) {
                    throw new InvalidConfigException("Provided setter method for \"" + this.getID() + "\" does not exist in " + this.getModID() + " config (" + this.configStorage.getClass().getName() + ").", e);
                }
            }
        }
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public String getID() {
        if (this.idPrefix.length != 0) {
            return String.join(":", this.idPrefix) + ":" + this.option.getName();
        }
        return this.option.getName();
    }

    @Override
    public @Nullable String getCategory() {
        return this.category;
    }

    @Override
    public void setCategory(@Nullable String category) {
        this.category = category;
    }

    @Override
    public String getModID() {
        return this.config.modID();
    }

    @Override
    public @NotNull Text getName() {
        if (this.name != null) {
            return new TranslatableText(this.name);
        }
        return SpeedrunOption.super.getName();
    }

    @Override
    public @Nullable Text getDescription() {
        if (this.description != null) {
            return new TranslatableText(this.description);
        }
        return SpeedrunOption.super.getDescription();
    }

    @Override
    public void setUnsafely(Object value) {
        try {
            if (this.setter != null) {
                this.setter.invoke(this.configStorage, value);
            }
            this.option.set(this.configStorage, value);
        } catch (ReflectiveOperationException e) {
            throw new ReflectionConfigException("Failed to set value for option " + this.getID() + " in " + this.getModID() + "config.", e);
        }
    }

    @Override
    public boolean hasWidget() {
        return !this.hide;
    }
}
