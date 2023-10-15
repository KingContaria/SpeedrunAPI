package org.mcsr.speedrunapi.config.api.annotations;

import org.mcsr.speedrunapi.config.api.SpeedrunConfig;
import org.mcsr.speedrunapi.config.screen.widgets.option.NumberOptionSliderWidget;
import org.mcsr.speedrunapi.config.screen.widgets.option.NumberOptionTextFieldWidget;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class Config {

    /**
     * Optional annotation that sets the name used in the config screen for the annotated option.
     * <p>
     * If an option is not using this annotation, "speedrunapi.config.modid.option.theOption" will be used as the translation key instead.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Name {

        /**
         * @return Returns the translation key for the annotated option's name.
         */
        String value();
    }

    /**
     * Optional annotation that sets the description used in the config screen for the annotated option.
     * <p>
     * If an option is not using this annotation, "speedrunapi.config.modid.option.theOption.description" will be used as the translation key instead.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Description {

        /**
         * @return Returns the translation key for the annotated option's description.
         */
        String value();

        /**
         * Removes the description used in the config screen for the annotated option.
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        @interface None {
        }
    }

    /**
     * Optional annotation that sets the category the used in the config screen for the annotated option.
     * <p>
     * The translation key used for the category name will be "speedrunapi.config.modid.category.theCategory".
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Category {

        /**
         * @return Returns the id of the config category.
         */
        String value();
    }

    /**
     * Optional annotation that sets Getter and/or Setter methods that will be used to access the annotated option instead of getting/setting it directly.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Access {

        /**
         * The method needs to be declared in the same {@link SpeedrunConfig} class as the annotated option.
         * It has to take no parameters and return the annotated option's type.
         *
         * @return Returns the name (not including parameters) of the Getter method that should be used for the annotated option.
         */
        String getter() default "";

        /**
         * The method needs to be declared in the same {@link SpeedrunConfig} class as the annotated option.
         * It has to take one parameter of the annotated option's type.
         *
         * @return Returns the name (not including parameters) of the Setter method that should be used for the annotated option.
         */
        String setter() default "";
    }

    public static class Numbers {

        public static class Fractional {

            /**
             * Required annotation for {@code float} and {@code double} options.
             * <p>
             * Sets the allowed bounds for the option.
             */
            @Retention(RetentionPolicy.RUNTIME)
            @Target(ElementType.FIELD)
            public @interface Bounds {

                /**
                 * @return Returns the minimum value for the annotated option.
                 */
                double min() default 0.0;

                /**
                 * @return Returns the maximum value for the annotated option.
                 */
                double max();

                /**
                 * @return Returns whether the bounds should be enforced or simply serve as boundaries for the gui widget.
                 */
                boolean enforce() default true;
            }

            /**
             * Optional annotation for {@code float} and {@code double} options.
             * <p>
             * Sets the intervals for the annotated option.
             */
            @Retention(RetentionPolicy.RUNTIME)
            @Target(ElementType.FIELD)
            public @interface Intervals {

                /**
                 * @return Returns the allowed intervals for the annotated option.
                 */
                double value();
            }
        }

        public static class Whole {

            /**
             * Required annotation for {@code short}, {@code int} and {@code long} options.
             * <p>
             * Sets the allowed bounds for the annotated option.
             */
            @Retention(RetentionPolicy.RUNTIME)
            @Target(ElementType.FIELD)
            public @interface Bounds {

                /**
                 * @return Returns the minimum value for the annotated option.
                 */
                long min() default 0L;

                /**
                 * @return Returns the maximum value for the annotated option.
                 */
                long max();

                /**
                 * @return Returns whether the bounds should be enforced or simply serve as boundaries for the gui widget.
                 */
                boolean enforce() default true;
            }

            /**
             * Optional annotation for {@code short}, {@code int}, {@code long}, {@code float} and {@code double} options.
             * <p>
             * Sets the intervals for the annotated option.
             */
            @Retention(RetentionPolicy.RUNTIME)
            @Target(ElementType.FIELD)
            public @interface Intervals {

                /**
                 * @return Returns the allowed intervals for the annotated option.
                 */
                long value();
            }
        }

        /**
         * Optional annotation for {@code short}, {@code int} and {@code long} options.
         * <p>
         * Sets the options widget in the config screen to be a {@link NumberOptionTextFieldWidget} instead of a {@link NumberOptionSliderWidget}.
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        public @interface TextField {
        }
    }

    public static class Strings {

        /**
         * Optional annotation for {@link String} options.
         * <p>
         * Sets the maximum amount of {@code char}'s the annotated String option is allowed to be.
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        public @interface MaxChars {

            /**
             * @return Returns the maximum amount of {@code char}'s the annotated String option is allowed to be.
             */
            int value();
        }
    }
}
