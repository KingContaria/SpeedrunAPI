package org.mcsr.speedrunapi.config.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class Config {

    /**
     * Sets the name used in the config screen for the annotated option.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Name {

        /**
         * @return Returns either translation key or literal name (if {@link Name#literal()} is {@code true}) for the annotated option.
         */
        String value();

        /**
         * @return Returns whether {@link Name#value()} is a translation key or literal.
         */
        boolean literal() default false;

        /**
         * Sets the name for the annotated option as translation of "speedrunapi.config.modid.option".
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        @interface Auto {
        }
    }

    /**
     * Sets the description used in the config screen for the annotated option.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Description {

        /**
         * @return Returns either translation key or literal description (if {@link Description#literal()} is {@code true}) for the annotated option.
         */
        String value();

        /**
         * @return Returns whether {@link Description#value()} is a translation key or literal.
         */
        boolean literal() default false;

        /**
         * Sets the description for the annotated option as translation of "speedrunapi.config.modid.option.description".
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        @interface Auto {
        }
    }

    public static class Numbers {

        public static class Fractional {

            /**
             * Required annotation for {@code float} and {@code double} options.
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
            }

            /**
             * Optional annotation for {@code float} and {@code double} options.
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
            }

            /**
             * Optional annotation for {@code short}, {@code int} and {@code long} options.
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
    }

    public static class Strings {

        /**
         * Optional annotation for {@link String} options
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
