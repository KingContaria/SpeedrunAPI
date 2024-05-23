package org.mcsr.speedrunapi.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.fabricmc.loader.api.ModContainer;
import org.mcsr.speedrunapi.SpeedrunAPI;
import org.mcsr.speedrunapi.config.api.SpeedrunConfig;
import org.mcsr.speedrunapi.config.api.SpeedrunOption;
import org.mcsr.speedrunapi.config.exceptions.NoSuchConfigException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public final class SpeedrunConfigContainer<T extends SpeedrunConfig> {

    private final T config;
    private final ModContainer mod;
    private final Map<String, SpeedrunOption<?>> options;

    SpeedrunConfigContainer(T config, ModContainer mod) {
        this.config = config;
        this.mod = mod;
        this.options = Collections.synchronizedMap(config.init());

        try {
            this.load();
        } catch (IOException | JsonParseException e) {
            SpeedrunAPI.LOGGER.warn("Failed to load config file for {}.", this.mod.getMetadata().getId(), e);
        }

        try {
            this.save();
        } catch (IOException e) {
            SpeedrunAPI.LOGGER.warn("Failed to save config file for {}.", this.mod.getMetadata().getId(), e);
        }
    }

    public void load() throws IOException, JsonParseException {
        File configFile = this.config.getConfigFile();

        if (!configFile.exists()) {
            return;
        }

        try (JsonReader reader = SpeedrunConfigAPI.GSON.newJsonReader(new FileReader(configFile))) {
            JsonObject configJson = SpeedrunConfigAPI.GSON.fromJson(reader, JsonObject.class);
            for (Map.Entry<String, JsonElement> entry : configJson.entrySet()) {
                SpeedrunOption<?> option = this.options.get(entry.getKey());
                if (option != null) {
                    try {
                        option.fromJson(entry.getValue());
                    } catch (ClassCastException | IllegalStateException e) {
                        SpeedrunAPI.LOGGER.warn("Failed to load the value for {} in {} config.", option.getID(), this.mod.getMetadata().getId());
                    }
                }
            }
        }
    }

    public void save() throws IOException {
        File configFile = this.config.getConfigFile();

        try (JsonWriter writer = SpeedrunConfigAPI.GSON.newJsonWriter(new FileWriter(configFile))) {
            writer.beginObject();
            for (Map.Entry<String, SpeedrunOption<?>> entry : this.options.entrySet()) {
                JsonElement value = entry.getValue().toJson();
                if (value != null) {
                    writer.name(entry.getKey());
                    this.writeJsonElement(writer, value);
                }
            }
            writer.endObject();
            writer.flush();
        }
    }

    private void writeJsonElement(JsonWriter writer, JsonElement jsonElement) throws IOException {
        if (jsonElement.isJsonObject()) {
            this.writeJsonObject(writer, jsonElement.getAsJsonObject());
        } else if (jsonElement.isJsonArray()) {
            this.writeJsonArray(writer, jsonElement.getAsJsonArray());
        } else {
            writer.jsonValue(jsonElement.toString());
        }
    }

    private void writeJsonObject(JsonWriter writer, JsonObject jsonObject) throws IOException {
        writer.beginObject();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            writer.name(entry.getKey());
            this.writeJsonElement(writer, entry.getValue());
        }
        writer.endObject();
    }

    private void writeJsonArray(JsonWriter writer, JsonArray jsonArray) throws IOException {
        writer.beginArray();
        for (JsonElement jsonElement : jsonArray) {
            this.writeJsonElement(writer, jsonElement);
        }
        writer.endArray();
    }

    public T getConfig() {
        return this.config;
    }

    public ModContainer getModContainer() {
        return this.mod;
    }

    public Collection<SpeedrunOption<?>> getOptions() {
        return this.options.values();
    }

    public SpeedrunOption<?> getOption(String name) throws NoSuchConfigException {
        SpeedrunOption<?> option = this.options.get(name);
        if (option == null) {
            throw new NoSuchConfigException("Could not find option \"" + name + "\" in " + this.mod.getMetadata().getId() + " config.");
        }
        return option;
    }
}
