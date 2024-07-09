package org.mcsr.speedrunapi;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mcsr.speedrunapi.config.SpeedrunConfigAPI;

public class SpeedrunAPI implements PreLaunchEntrypoint, ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onPreLaunch() {
        SpeedrunConfigAPI.onPreLaunch();
    }

    @Override
    public void onInitialize() {
        SpeedrunConfigAPI.onInitialize();
    }
}
