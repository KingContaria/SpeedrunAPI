package org.mcsr.speedrunapi.config.api;

import org.jetbrains.annotations.Nullable;
import org.mcsr.speedrunapi.config.api.annotations.Config;
import org.mcsr.speedrunapi.config.api.annotations.NoConfig;
import org.mcsr.speedrunapi.config.exceptions.SpeedrunConfigAPIException;
import org.mcsr.speedrunapi.config.exceptions.UnsupportedConfigException;
import org.mcsr.speedrunapi.config.option.*;

import java.lang.reflect.Field;
import java.util.*;

public interface SpeedrunConfigStorage {

    default Map<String, SpeedrunOption<?>> init(SpeedrunConfig config, String... optionIDPrefix) {
        Map<String, SpeedrunOption<?>> options = new LinkedHashMap<>();

        List<Class<?>> classes = new ArrayList<>();
        for (Class<?> clas = this.getClass(); clas != null; clas = clas.getSuperclass()) {
            classes.add(clas);
        }
        Collections.reverse(classes);

        for (Class<?> clas : classes) {
            for (Field field : clas.getDeclaredFields()) {
                if (field.isAnnotationPresent(NoConfig.class)) {
                    continue;
                }

                Class<?> type = field.getType();
                SpeedrunOption<?> option = this.parseField(field, config, optionIDPrefix);
                if (option != null) {
                    options.put(option.getID(), option);
                    continue;
                }

                if (SpeedrunConfigStorage.class.isAssignableFrom(type) && !SpeedrunConfig.class.isAssignableFrom(type)) {
                    try {
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
                    } catch (IllegalAccessException | NullPointerException e) {
                        throw new SpeedrunConfigAPIException(e);
                    }
                } else {
                    String id = field.getName();
                    if (optionIDPrefix.length != 0) {
                        id = String.join(":", optionIDPrefix) + ":" + id;
                    }
                    throw new UnsupportedConfigException("Option " + id + " is of an unsupported type (" + type + ") in " + config.modID() + " config.");
                }
            }
        }
        return options;
    }

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
