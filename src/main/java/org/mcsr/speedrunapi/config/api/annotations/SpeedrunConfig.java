package org.mcsr.speedrunapi.config.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SpeedrunConfig {

    String modID();

    InitPoint initializeOn() default InitPoint.ONINITIALIZE;

    enum InitPoint {
        PRELAUNCH,
        ONINITIALIZE,
        POSTLAUNCH
    }
}
