package org.mcsr.speedrunapi.config.option;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mcsr.speedrunapi.config.api.SpeedrunConfig;
import org.mcsr.speedrunapi.config.api.annotations.Config;
import org.mcsr.speedrunapi.config.exceptions.InvalidConfigException;
import org.mcsr.speedrunapi.config.exceptions.SpeedrunConfigAPIException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class BaseOption<T> extends Option<T> {

    protected final SpeedrunConfig config;
    protected final Field option;

    @Nullable
    protected final Config.Category category;
    @Nullable
    protected final Method getter;
    @Nullable
    protected final Method setter;

    public BaseOption(SpeedrunConfig config, Field option) {
        this.config = config;
        this.option = option;
        this.option.setAccessible(true);

        this.category = option.getAnnotation(Config.Category.class);

        Method getter = null;
        Method setter = null;
        Config.Access access = option.getAnnotation(Config.Access.class);
        if (access != null) {
            if (!access.getter().isEmpty()) {
                try {
                    getter = this.config.getClass().getDeclaredMethod(access.getter());
                    if (!getter.getReturnType().equals(option.getType())) {
                        throw new InvalidConfigException("Provided getter method for \"" + this.getID() + "\" does not exist in " + this.getModID() + ".");
                    }
                    getter.setAccessible(true);
                } catch (NoSuchMethodException e) {
                    throw new InvalidConfigException("Provided getter method for \"" + this.getID() + "\" does not exist in " + this.getModID() + ".", e);
                }
            }
            if (!access.setter().isEmpty()) {
                try {
                    setter = this.config.getClass().getDeclaredMethod(access.setter(), option.getType());
                    setter.setAccessible(true);
                } catch (NoSuchMethodException e) {
                    throw new InvalidConfigException("Provided setter method for \"" + this.getID() + "\" does not exist in " + this.getModID() + ".", e);
                }
            }
        }
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public String getID() {
        return this.option.getName();
    }

    @Override
    public String getCategory() {
        return this.category != null ? this.category.value() : null;
    }

    @Override
    public String getModID() {
        return this.config.modID();
    }

    @Override
    public @NotNull Text getName() {
        Config.Name name = this.option.getAnnotation(Config.Name.class);
        if (name != null) {
            return new TranslatableText(name.value());
        }
        return super.getName();
    }

    @Override
    public @Nullable Text getDescription() {
        Config.Description description = this.option.getAnnotation(Config.Description.class);
        if (description != null) {
            return new TranslatableText(description.value());
        }
        if (this.option.isAnnotationPresent(Config.Description.None.class)) {
            return null;
        }
        return super.getDescription();
    }

    @Override
    public void setUnsafely(Object value) {
        try {
            if (this.setter != null) {
                this.setter.invoke(this.config, value);
            }
            this.option.set(this.config, value);
        } catch (ReflectiveOperationException e) {
            throw new SpeedrunConfigAPIException(e);
        }
    }
}
