package org.mcsr.speedrunapi.config.api;

import org.mcsr.speedrunapi.config.api.annotations.NoConfig;
import org.mcsr.speedrunapi.config.exceptions.UnsupportedConfigException;
import org.mcsr.speedrunapi.config.option.*;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Provides a custom config screen, can also be used by mods using their own config system to show up in the config list.
 * <p>
 * Register by adding your class implementing {@link SpeedrunConfig} to your mod's fabric.mod.json like this:
 * <p>
 * "custom": [ "speedrunapi": [ "config": "a.b.c.ABCConfig" ] ]
 */
public interface SpeedrunConfig {

    /**
     * @return Returns the mod ID of the mod owning the config.
     */
    String modID();

    /**
     * Initializes the config, creating all the {@link Option}'s it provides.
     * <p>
     * Mod Authors can override this method to add {@link CustomOption}'s.
     *
     * @return Returns a {@link Map} of all of this configs {@link Option}'s mapped to their ID's.
     */
    default Map<String, Option<?>> init() {
        Map<String, Option<?>> options = new LinkedHashMap<>();
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(NoConfig.class)) {
                continue;
            }

            Class<?> type = field.getType();
            Option<?> option;
            if (boolean.class.equals(type)) {
                option = new BooleanOption(this, field);
            } else if (short.class.equals(type)) {
                option = new ShortOption(this, field);
            } else if (int.class.equals(type)) {
                option = new IntegerOption(this, field);
            } else if (long.class.equals(type)) {
                option = new LongOption(this, field);
            } else if (float.class.equals(type)) {
                option = new FloatOption(this, field);
            } else if (double.class.equals(type)) {
                option = new DoubleOption(this, field);
            } else if (String.class.equals(type)) {
                option = new StringOption(this, field);
            } else if (Enum.class.isAssignableFrom(type)) {
                option = new EnumOption(this, field);
            } else {
                throw new UnsupportedConfigException();
            }
            options.put(field.getName(), option);
        }
        return options;
    }
}
