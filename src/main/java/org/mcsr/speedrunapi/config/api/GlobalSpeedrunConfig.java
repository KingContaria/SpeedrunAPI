package org.mcsr.speedrunapi.config.api;

import org.mcsr.speedrunapi.config.SpeedrunConfigAPI;

import java.io.File;
import java.nio.file.Files;

public interface GlobalSpeedrunConfig extends SpeedrunConfig {

    @Override
    default File getConfigFile() {
        if (Files.exists(SpeedrunConfigAPI.getConfigDir().resolve(this.modID() + ".global"))) {
            return SpeedrunConfigAPI.getGlobalConfigDir().resolve(this.modID() + ".json").toFile();
        }
        return SpeedrunConfig.super.getConfigFile();
    }
}
