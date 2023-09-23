package org.mcsr.speedrunapi.config.api.annotations;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.minecraft.client.MinecraftClient;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Required annotation for config classes.
 * <p>
 * Register by adding your config class to your mod's fabric.mod.json like this:
 * <p>
 * "custom": [ "speedrunapi": [ "config": "a.b.c.Config" ] ]
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SpeedrunConfig {

    /**
     * @return Returns the mod ID of the config.
     */
    String modID();

    /**
     * @return Returns at what stage of Minecraft's launch the config should be initialized.
     */
    InitPoint initializeOn() default InitPoint.ONINITIALIZE;

    enum InitPoint {
        /**
         * Activates on {@link PreLaunchEntrypoint#onPreLaunch()}.
         */
        PRELAUNCH,

        /**
         * Activates on {@link ModInitializer#onInitialize()}.
         */
        ONINITIALIZE,

        /**
         * Activates when {@link MinecraftClient} finishes initialization.
         */
        POSTLAUNCH;
    }
}
