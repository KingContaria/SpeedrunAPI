package org.mcsr.speedrunapi.properties.api.annotations;

import org.mcsr.speedrunapi.properties.api.SpeedrunProperties;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class Property {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Restrictions {

        boolean gettable() default true;

        boolean settable() default false;
    }

    /**
     * Optional annotation that sets Getter and/or Setter methods that will be used to access the annotated property instead of getting/setting it directly.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Access {

        /**
         * The method needs to be declared in the same {@link SpeedrunProperties} class as the annotated property.
         * It has to take no parameters and return the annotated property's type.
         *
         * @return Returns the name (not including parameters) of the Getter method that should be used for the annotated property.
         */
        String getter() default "";


        /**
         * The method needs to be declared in the same {@link SpeedrunProperties} class as the annotated property.
         * It has to take one parameter of the annotated property's type.
         *
         * @return Returns the name (not including parameters) of the Setter method that should be used for the annotated property.
         */
        String setter() default "";
    }
}
