package me.contaria.speedrunapi.config;

import me.contaria.speedrunapi.config.api.SpeedrunConfigParsedMetadata;
import net.fabricmc.loader.api.Version;
import org.jetbrains.annotations.Nullable;

class SpeedrunConfigParsedMetadataImpl implements SpeedrunConfigParsedMetadata {
    @Nullable
    private final Version apiVersion;
    @Nullable
    private final Version modVersion;
    private final int dataVersion;

    SpeedrunConfigParsedMetadataImpl(@Nullable Version apiVersion, @Nullable Version modVersion, int dataVersion) {
        this.apiVersion = apiVersion;
        this.modVersion = modVersion;
        this.dataVersion = dataVersion;
    }

    @Override
    public @Nullable Version getApiVersion() {
        return this.apiVersion;
    }

    @Override
    public @Nullable Version getModVersion() {
        return this.modVersion;
    }

    @Override
    public int getDataVersion() {
        return this.dataVersion;
    }
}
