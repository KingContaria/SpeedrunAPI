package me.contaria.speedrunapi.config.api;

import net.fabricmc.loader.api.Version;
import org.jetbrains.annotations.Nullable;

public interface SpeedrunConfigParsedMetadata {
    /**
     * @return The version of SpeedrunAPI the loaded config data was saved in.
     *         Returns {@code null} if no version was saved or if the version failed to be parsed.
     */
    @Nullable
    Version getApiVersion();

    /**
     * @return The version of the configs parent mod the loaded config data was saved in.
     *         Returns {@code null} if no version was saved or if the version failed to be parsed.
     */
    @Nullable
    Version getModVersion();

    /**
     * @return The data version of the config the loaded config data was saved in.
     *         Returns {@code 0} if no version was saved or if the version failed to be parsed.
     *
     * @see SpeedrunConfig#getDataVersion
     */
    int getDataVersion();
}
