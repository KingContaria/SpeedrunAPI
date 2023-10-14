package org.mcsr.speedrunapi.config.api.annotations;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.minecraft.client.MinecraftClient;
import org.mcsr.speedrunapi.config.api.SpeedrunConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Optional annotation for {@link SpeedrunConfig}'s to specify the desired initialization point for the config.
 * <p>
 * If this annotation is not used, the initialization point will default to {@link InitPoint#ONINITIALIZE}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface InitializeOn {

    InitPoint value() default InitPoint.ONINITIALIZE;

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
        POSTLAUNCH
    }
}
