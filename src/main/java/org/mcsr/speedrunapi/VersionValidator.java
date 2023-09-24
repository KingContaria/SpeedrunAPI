package org.mcsr.speedrunapi;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class VersionValidator implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        String modTargetVersion = FabricLoader.getInstance().getModContainer("speedrunapi").get().getMetadata().getCustomValue("speedrunapi-mcversion").getAsString();
        String minecraftVersion = FabricLoader.getInstance().getModContainer("minecraft").get().getMetadata().getVersion().getFriendlyString();
        if (!FabricLoader.getInstance().isDevelopmentEnvironment() && !modTargetVersion.equals(minecraftVersion)) {
            RuntimeException exception = new RuntimeException(String.format("[SPEEDRUNAPI ERRROR!] The mod's depend version and minecraft version do not match!\nTargeted Version: %s\nMinecraft Version: %s\nYou should download the SpeedrunAPI for Minecraft %s", modTargetVersion, minecraftVersion, minecraftVersion));
            exception.setStackTrace(new StackTraceElement[0]);
            throw exception;
        }
    }
}
