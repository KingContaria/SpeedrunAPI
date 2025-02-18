package me.contaria.speedrunapi.config.api;

import me.contaria.speedrunapi.config.SpeedrunConfigAPI;
import me.contaria.speedrunapi.config.api.annotations.Config;
import me.contaria.speedrunapi.config.exceptions.InvalidConfigException;
import me.contaria.speedrunapi.config.option.*;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public interface SpeedrunConfigStorage {

    /**
     * Creates the {@link SpeedrunOption}'s for this config storage.
     *
     * @param config         - The config the options are being generated from.
     * @param optionIDPrefix - ID Prefixes for the generated options.
     * @return Returns a {@link Map} of all of this config storages {@link SpeedrunOption}'s mapped to their ID's.
     * @throws InvalidConfigException If any fields type is not supported, and is not annotated with {@link Config.Ignored}.
     */
    default Map<String, SpeedrunOption<?>> init(SpeedrunConfig config, String... optionIDPrefix) throws ReflectiveOperationException {
        Map<String, SpeedrunOption<?>> options = new LinkedHashMap<>();

        List<Class<?>> classes = new ArrayList<>();
        for (Class<?> clas = this.getClass(); clas != null; clas = clas.getSuperclass()) {
            classes.add(clas);
        }
        Collections.reverse(classes);

        for (Class<?> clas : classes) {
            for (Field field : clas.getDeclaredFields()) {
                if (field.isAnnotationPresent(Config.Ignored.class)) {
                    continue;
                }

                if (!config.shouldParseStaticFields() && Modifier.isStatic(field.getModifiers())) {
                    continue;
                }

                Class<?> type = field.getType();
                SpeedrunOption<?> option = this.parseField(field, config, optionIDPrefix);
                if (option != null) {
                    options.put(option.getID(), option);
                    continue;
                }

                if (!(SpeedrunConfigStorage.class.isAssignableFrom(type) && !SpeedrunConfig.class.isAssignableFrom(type))) {
                    String id = field.getName();
                    if (optionIDPrefix.length != 0) {
                        id = String.join(":", optionIDPrefix) + ":" + id;
                    }
                    throw new InvalidConfigException("Option " + id + " is of an unsupported type (" + type + ") in " + config.modID() + " config.");
                }

                field.setAccessible(true);

                String[] updatedOptionIDPrefix = new String[optionIDPrefix.length + 1];
                System.arraycopy(optionIDPrefix, 0, updatedOptionIDPrefix, 0, optionIDPrefix.length);
                updatedOptionIDPrefix[updatedOptionIDPrefix.length - 1] = field.getName();

                Map<String, SpeedrunOption<?>> configDataOptions = ((SpeedrunConfigStorage) field.get(this)).init(config, updatedOptionIDPrefix);

                Config.Category category = field.getAnnotation(Config.Category.class);
                if (category != null) {
                    for (SpeedrunOption<?> o : configDataOptions.values()) {
                        if (o.getCategory() == null) {
                            o.setCategory(category.value());
                        }
                    }
                }
                options.putAll(configDataOptions);
            }
        }
        return options;
    }

    /**
     * Parses a field to a {@link SpeedrunOption}.
     * <p>
     * Mod authors can override this method to add support for custom field types using a {@link SpeedrunConfigAPI.CustomOption.Builder}.
     *
     * @param field    - The field in this class that is being parsed.
     * @param config   - The config the option is being generated from.
     * @param idPrefix - ID Prefixes for the generated option.
     * @return A {@link SpeedrunOption} for the field or null if the field type is not supported.
     */
    default @Nullable SpeedrunOption<?> parseField(Field field, SpeedrunConfig config, String... idPrefix) {
        Class<?> type = field.getType();
        if (boolean.class.equals(type)) {
            return new BooleanOption(config, this, field, idPrefix);
        } else if (short.class.equals(type)) {
            return new ShortOption(config, this, field, idPrefix);
        } else if (int.class.equals(type)) {
            return new IntegerOption(config, this, field, idPrefix);
        } else if (long.class.equals(type)) {
            return new LongOption(config, this, field, idPrefix);
        } else if (float.class.equals(type)) {
            return new FloatOption(config, this, field, idPrefix);
        } else if (double.class.equals(type)) {
            return new DoubleOption(config, this, field, idPrefix);
        } else if (String.class.equals(type)) {
            return new StringOption(config, this, field, idPrefix);
        } else if (type.isEnum()) {
            return new EnumOption(config, this, field, idPrefix);
        }
        return null;
    }
}
