package org.mcsr.speedrunapi.config.api.annotations;

import org.mcsr.speedrunapi.config.screen.SpeedrunConfigScreen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for fields in config classes that are not configurable.
 * <p>
 * The field will not be available from the {@link SpeedrunConfigScreen} and will not be saved in the config file.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NoConfig {
}
