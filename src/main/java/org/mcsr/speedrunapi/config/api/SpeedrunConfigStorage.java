package org.mcsr.speedrunapi.config.api;

import org.mcsr.speedrunapi.config.api.annotations.Config;
import org.mcsr.speedrunapi.config.api.annotations.NoConfig;
import org.mcsr.speedrunapi.config.exceptions.SpeedrunConfigAPIException;
import org.mcsr.speedrunapi.config.exceptions.UnsupportedConfigException;
import org.mcsr.speedrunapi.config.option.*;

import java.lang.reflect.Field;
import java.util.*;

public interface SpeedrunConfigStorage {

    default Map<String, Option<?>> init(SpeedrunConfig config, String optionIDPrefix) {
        Map<String, Option<?>> options = new LinkedHashMap<>();

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
                BaseOption<?> option;
                if (boolean.class.equals(type)) {
                    option = new BooleanOption(config, this, field);
                } else if (short.class.equals(type)) {
                    option = new ShortOption(config, this, field);
                } else if (int.class.equals(type)) {
                    option = new IntegerOption(config, this, field);
                } else if (long.class.equals(type)) {
                    option = new LongOption(config, this, field);
                } else if (float.class.equals(type)) {
                    option = new FloatOption(config, this, field);
                } else if (double.class.equals(type)) {
                    option = new DoubleOption(config, this, field);
                } else if (String.class.equals(type)) {
                    option = new StringOption(config, this, field);
                } else if (type.isEnum()) {
                    option = new EnumOption(config, this, field);
                } else if (SpeedrunConfigStorage.class.isAssignableFrom(type) && !SpeedrunConfig.class.isAssignableFrom(type)) {
                    try {
                        field.setAccessible(true);
                        Map<String, Option<?>> configDataOptions = ((SpeedrunConfigStorage) field.get(this)).init(config, optionIDPrefix + field.getName() + ":");
                        Config.Category category = field.getAnnotation(Config.Category.class);
                        if (category != null) {
                            for (Option<?> o : configDataOptions.values()) {
                                if (o.getCategory() == null) {
                                    o.setCategory(category.value());
                                }
                            }
                        }
                        options.putAll(configDataOptions);
                        continue;
                    } catch (IllegalAccessException | NullPointerException e) {
                        throw new SpeedrunConfigAPIException(e);
                    }
                } else {
                    throw new UnsupportedConfigException("Option " + optionIDPrefix + field.getName() + " is of an unsupported type (" + type + ") in " + config.modID() + " config.");
                }
                option.setIDPrefix(optionIDPrefix);
                options.put(optionIDPrefix + field.getName(), option);
            }
        }
        return options;
    }
}
