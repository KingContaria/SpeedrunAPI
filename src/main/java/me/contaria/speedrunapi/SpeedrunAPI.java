package me.contaria.speedrunapi;

import me.contaria.speedrunapi.config.SpeedrunConfigAPI;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SpeedrunAPI implements PreLaunchEntrypoint, ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final ModContainer MOD_CONTAINER = FabricLoader.getInstance().getModContainer("speedrunapi").orElseThrow(RuntimeException::new);

    @Override
    public void onPreLaunch() {
        SpeedrunConfigAPI.onPreLaunch();
    }

    @Override
    public void onInitialize() {
        SpeedrunConfigAPI.onInitialize();
    }
}
