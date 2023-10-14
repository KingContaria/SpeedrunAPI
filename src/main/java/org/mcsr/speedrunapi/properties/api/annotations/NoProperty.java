package org.mcsr.speedrunapi.properties.api.annotations;

import org.mcsr.speedrunapi.properties.api.SpeedrunProperties;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for fields in {@link SpeedrunProperties} classes that are not accessible.
 * The field will not be available for other mods to read or write to.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NoProperty {
}
