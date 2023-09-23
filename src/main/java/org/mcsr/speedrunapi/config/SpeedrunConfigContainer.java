package org.mcsr.speedrunapi.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.gui.screen.Screen;
import org.mcsr.speedrunapi.SpeedrunAPI;
import org.mcsr.speedrunapi.config.api.annotations.NoConfig;
import org.mcsr.speedrunapi.config.option.*;
import org.mcsr.speedrunapi.config.screen.SpeedrunConfigScreen;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SpeedrunConfigContainer<T> {

    private final T config;
    private final ModContainer mod;
    private final Map<String, Option<?>> options = Collections.synchronizedMap(new HashMap<>());

    protected SpeedrunConfigContainer(T config, ModContainer mod) {
        this.config = config;
        this.mod = mod;

        this.init();

        try {
            this.load();
        } catch (IOException | JsonParseException e) {
            SpeedrunAPI.LOGGER.warn("Failed to load config file for {}", this.mod.getMetadata().getId(), e);
        }

        try {
            this.save();
        } catch (IOException e) {
            SpeedrunAPI.LOGGER.warn("Failed to save config file for {}", this.mod.getMetadata().getId(), e);
        }
    }

    private void init() {
        for (Field field : this.config.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(NoConfig.class)) {
                continue;
            }

            Class<?> type = field.getType();
            Option<?> option;
            if (boolean.class.equals(type)) {
                option = new BooleanOption(this.config, field);
            } else if (short.class.equals(type)) {
                option = new ShortOption(this.config, field);
            } else if (int.class.equals(type)) {
                option = new IntegerOption(this.config, field);
            } else if (long.class.equals(type)) {
                option = new LongOption(this.config, field);
            } else if (float.class.equals(type)) {
                option = new FloatOption(this.config, field);
            } else if (double.class.equals(type)) {
                option = new DoubleOption(this.config, field);
            } else if (String.class.equals(type)) {
                option = new StringOption(this.config, field);
            } else if (Enum.class.isAssignableFrom(type)) {
                option = new EnumOption(this.config, field);
            } else {
                continue;
            }
            this.options.put(field.getName(), option);
        }
    }

    public ModContainer getModContainer() {
        return this.mod;
    }

    public T getConfig() {
        return this.config;
    }

    protected void load() throws IOException, JsonParseException {
        File configFile = this.getConfigFile();

        if (!configFile.exists()) {
            return;
        }

        try (JsonReader reader = SpeedrunConfigAPI.GSON.newJsonReader(new FileReader(configFile))) {
            JsonObject configJson = SpeedrunConfigAPI.GSON.fromJson(reader, JsonObject.class);
            for (Map.Entry<String, JsonElement> entry : configJson.entrySet()) {
                Option<?> option = this.options.get(entry.getKey());
                if (option != null) {
                    try {
                        option.fromJson(entry.getValue());
                    } catch (ClassCastException e) {
                        SpeedrunAPI.LOGGER.warn("Failed to load the value for {} in {}", option.getID(), this.mod.getMetadata().getId());
                    }
                }
            }
        }
    }

    public void save() throws IOException {
        File configFile = this.getConfigFile();

        configFile.getParentFile().mkdirs();

        try (JsonWriter writer = SpeedrunConfigAPI.GSON.newJsonWriter(new FileWriter(configFile))) {
            writer.beginObject();
            for (Map.Entry<String, Option<?>> entry : this.options.entrySet()) {
                writer.name(entry.getKey());
                writer.jsonValue(entry.getValue().toJson().toString());
            }
            writer.endObject();
            writer.flush();
        }
    }

    private File getConfigFile() {
        return SpeedrunConfigAPI.CONFIG_DIR.resolve(this.mod.getMetadata().getId() + ".json").toFile();
    }

    public Collection<Option<?>> getOptions() {
        return this.options.values();
    }

    public Screen createConfigScreen(Screen parent) {
        return new SpeedrunConfigScreen(this, parent);
    }
}
