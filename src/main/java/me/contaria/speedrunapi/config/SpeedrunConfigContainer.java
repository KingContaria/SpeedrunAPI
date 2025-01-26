package me.contaria.speedrunapi.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;
import me.contaria.speedrunapi.SpeedrunAPI;
import me.contaria.speedrunapi.config.api.SpeedrunConfig;
import me.contaria.speedrunapi.config.api.SpeedrunOption;
import me.contaria.speedrunapi.config.exceptions.NoSuchConfigException;
import me.contaria.speedrunapi.config.exceptions.SpeedrunConfigAPIException;
import net.fabricmc.loader.api.ModContainer;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public final class SpeedrunConfigContainer<T extends SpeedrunConfig> {
    private final T config;
    private final ModContainer mod;
    private final Map<String, SpeedrunOption<?>> options;
    private final int dataVersion;

    SpeedrunConfigContainer(T config, ModContainer mod) throws ReflectiveOperationException {
        this.config = config;
        this.mod = mod;
        this.options = Collections.synchronizedMap(config.init());
        this.dataVersion = config.getDataVersion();

        try {
            this.load();
        } catch (IOException | JsonParseException e) {
            SpeedrunAPI.LOGGER.warn("Failed to load config file for {}.", this.config.modID(), e);
        }

        try {
            this.save();
        } catch (IOException e) {
            SpeedrunAPI.LOGGER.warn("Failed to save config file for {}.", this.config.modID(), e);
        }
    }

    public void load() throws IOException, JsonParseException {
        File configFile = this.config.getConfigFile();

        if (!configFile.exists()) {
            return;
        }

        this.config.preLoad();

        try (JsonReader reader = SpeedrunConfigAPI.GSON.newJsonReader(new FileReader(configFile))) {
            JsonObject jsonObject = SpeedrunConfigAPI.GSON.fromJson(reader, JsonObject.class);
            int dataVersion = jsonObject.has("dataVersion") ? jsonObject.remove("dataVersion").getAsInt() : 0;

            this.config.onLoad(jsonObject, dataVersion);
            this.fromJson(jsonObject);
        }

        this.config.finishLoading();
    }

    public void save() throws IOException {
        File configFile = this.config.getConfigFile();

        this.config.preSave();

        JsonObject jsonObject = this.toJson();
        this.config.onSave(jsonObject);
        jsonObject = this.addMetadata(jsonObject);

        Files.write(configFile.toPath(), SpeedrunConfigAPI.GSON.toJson(jsonObject).getBytes(StandardCharsets.UTF_8));

        this.config.finishSaving();
    }

    private JsonObject addMetadata(JsonObject jsonObject) {
        JsonObject result = new JsonObject();

        // add internal SpeedrunAPI metadata
        result.add(".dataVersion", new JsonPrimitive(this.dataVersion));

        // check and re-add entries from original jsonObject
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(".")) {
                throw new SpeedrunConfigAPIException("Invalid config entry: " + key + " (Entries starting with '.' are reserved for SpeedrunAPI internals)");
            }
            result.add(key, entry.getValue());
        }

        return result;
    }

    /**
     * @since 1.1
     */
    public void fromJson(JsonObject jsonObject) {
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            SpeedrunOption<?> option = this.options.get(entry.getKey());
            if (option != null) {
                try {
                    option.fromJson(entry.getValue());
                } catch (ClassCastException | IllegalStateException e) {
                    SpeedrunAPI.LOGGER.warn("Failed to load the value for {} in {} config.", option.getID(), this.config.modID());
                }
            }
        }
    }

    /**
     * @since 1.1
     */
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        for (Map.Entry<String, SpeedrunOption<?>> entry : this.options.entrySet()) {
            JsonElement value = entry.getValue().toJson();
            if (value != null) {
                jsonObject.add(entry.getKey(), value);
            }
        }
        return jsonObject;
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
            throw new NoSuchConfigException("Could not find option \"" + name + "\" in " + this.config.modID() + " config.");
        }
        return option;
    }

    static final class Uninitialized<T extends SpeedrunConfig> implements Comparable<Uninitialized<T>> {
        final Class<T> config;
        final ModContainer mod;
        final int priority;

        Uninitialized(Class<T> config, ModContainer mod, int priority) {
            this.config = config;
            this.mod = mod;
            this.priority = priority;
        }

        @Override
        public int compareTo(@NotNull SpeedrunConfigContainer.Uninitialized<T> o) {
            if (this.priority != o.priority) {
                return this.priority > o.priority ? 1 : -1;
            }
            return this.mod.getMetadata().getId().compareTo(o.mod.getMetadata().getId());
        }
    }
}
