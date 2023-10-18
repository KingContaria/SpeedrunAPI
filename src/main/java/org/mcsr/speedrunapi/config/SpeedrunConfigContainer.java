package org.mcsr.speedrunapi.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.fabricmc.loader.api.ModContainer;
import org.mcsr.speedrunapi.SpeedrunAPI;
import org.mcsr.speedrunapi.config.api.SpeedrunConfig;
import org.mcsr.speedrunapi.config.exceptions.NoSuchConfigException;
import org.mcsr.speedrunapi.config.option.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class SpeedrunConfigContainer<T extends SpeedrunConfig> {

    private final T config;
    private final ModContainer mod;
    private final Map<String, Option<?>> options;

    protected SpeedrunConfigContainer(T config, ModContainer mod) {
        this.config = config;
        this.mod = mod;
        this.options = Collections.synchronizedMap(config.init());

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

    public ModContainer getModContainer() {
        return this.mod;
    }

    public T getConfig() {
        return this.config;
    }

    protected void load() throws IOException, JsonParseException {
        File configFile = this.config.getConfigFile();

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
        File configFile = this.config.getConfigFile();

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

    public Collection<Option<?>> getOptions() {
        return this.options.values();
    }

    public Option<?> getOption(String name) throws NoSuchConfigException {
        Option<?> option = this.options.get(name);
        if (option == null) {
            throw new NoSuchConfigException("Could not find option \"" + name + "\" in " + this.mod.getMetadata().getId() + " config.");
        }
        return option;
    }
}
