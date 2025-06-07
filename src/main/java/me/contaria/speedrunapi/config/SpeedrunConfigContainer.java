package me.contaria.speedrunapi.config;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import me.contaria.speedrunapi.SpeedrunAPI;
import me.contaria.speedrunapi.config.api.SpeedrunConfig;
import me.contaria.speedrunapi.config.api.SpeedrunConfigParsedMetadata;
import me.contaria.speedrunapi.config.api.SpeedrunOption;
import me.contaria.speedrunapi.config.exceptions.NoSuchConfigException;
import me.contaria.speedrunapi.config.exceptions.SpeedrunConfigAPIException;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.throwables.MixinException;

import java.io.*;
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

        try {
            this.config.preLoad();

            try (JsonReader reader = SpeedrunConfigAPI.GSON.newJsonReader(new InputStreamReader(Files.newInputStream(configFile.toPath()), StandardCharsets.UTF_8))) {
                JsonObject jsonObject = SpeedrunConfigAPI.GSON.fromJson(reader, JsonObject.class);
                SpeedrunConfigParsedMetadata metadata = this.removeMetadata(jsonObject);

                this.config.onLoad(jsonObject, metadata);
                this.fromJson(jsonObject);
            } catch (MixinException e) {
                throw e;
            } catch (Exception e) {
                this.config.handleLoadException(e);
            }

            this.config.finishLoading();
        } catch (Exception e) {
            throw new SpeedrunConfigAPIException("Failed to load " + this.config.modID() + " config!", e);
        }
    }

    private SpeedrunConfigParsedMetadata removeMetadata(JsonObject jsonObject) {
        return new SpeedrunConfigParsedMetadataImpl(
                this.removeAndGetVersionFromJson(jsonObject, ".apiVersion"),
                this.removeAndGetVersionFromJson(jsonObject, ".modVersion"),
                this.removeAndGetIntFromJson(jsonObject, ".dataVersion")
        );
    }

    private Version removeAndGetVersionFromJson(JsonObject jsonObject, String name) {
        if (!jsonObject.has(name)) {
            return null;
        }
        JsonElement jsonElement = jsonObject.remove(name);
        if (!jsonElement.isJsonPrimitive()) {
            return null;
        }
        JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
        if (!jsonPrimitive.isString()) {
            return null;
        }
        try {
            return Version.parse(jsonElement.getAsString());
        } catch (VersionParsingException e) {
            return null;
        }
    }

    private int removeAndGetIntFromJson(JsonObject jsonObject, String name) {
        if (!jsonObject.has(name)) {
            return 0;
        }
        JsonElement jsonElement = jsonObject.remove(name);
        if (!jsonElement.isJsonPrimitive()) {
            return 0;
        }
        JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
        if (!jsonPrimitive.isNumber()) {
            return 0;
        }
        return jsonElement.getAsInt();
    }

    public void save() throws IOException {
        File configFile = this.config.getConfigFile();

        try {
            this.config.preSave();

            try {
                JsonObject jsonObject = this.toJson();
                this.config.onSave(jsonObject);
                jsonObject = this.addMetadata(jsonObject);

                Files.write(configFile.toPath(), SpeedrunConfigAPI.GSON.toJson(jsonObject).getBytes(StandardCharsets.UTF_8));
            } catch (MixinException e) {
                throw e;
            } catch (Exception e) {
                this.config.handleSaveException(e);
            }

            this.config.finishSaving();
        } catch (MixinException e) {
            throw e;
        } catch (Exception e) {
            throw new SpeedrunConfigAPIException("Failed to save " + this.config.modID() + " config!", e);
        }
    }

    private JsonObject addMetadata(JsonObject jsonObject) {
        JsonObject result = new JsonObject();

        // add internal SpeedrunAPI metadata
        result.add(".apiVersion", new JsonPrimitive(SpeedrunAPI.MOD_CONTAINER.getMetadata().getVersion().getFriendlyString()));
        result.add(".modVersion", new JsonPrimitive(this.mod.getMetadata().getVersion().getFriendlyString()));
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

    public void fromJson(JsonObject jsonObject) {
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            SpeedrunOption<?> option = this.options.get(entry.getKey());
            if (option == null) {
                continue;
            }

            JsonElement jsonElement = entry.getValue();
            try {
                try {
                    option.fromJson(jsonElement);
                } catch (MixinException e) {
                    throw e;
                } catch (Exception e) {
                    this.config.handleLoadException(e, option, jsonElement);
                }
            } catch (Exception e) {
                throw new SpeedrunConfigAPIException("Failed to load the value for " + option.getID() + " in " + this.config.modID() + " config: " + jsonElement, e);
            }
        }
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        for (Map.Entry<String, SpeedrunOption<?>> entry : this.options.entrySet()) {
            SpeedrunOption<?> option = entry.getValue();

            JsonElement jsonElement;
            try {
                try {
                    jsonElement = option.toJson();
                } catch (MixinException e) {
                    throw e;
                } catch (Exception e) {
                    this.config.handleSaveException(e, option);
                    continue;
                }
            } catch (MixinException e) {
                throw e;
            } catch (Exception e) {
                throw new SpeedrunConfigAPIException("Failed to save the value for " + option.getID() + " in " + this.config.modID() + " config: " + option.get(), e);
            }

            if (jsonElement != null) {
                jsonObject.add(entry.getKey(), jsonElement);
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
