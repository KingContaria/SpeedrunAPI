package org.mcsr.speedrunapi.config.api.annotations;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.minecraft.client.MinecraftClient;
import org.mcsr.speedrunapi.config.api.SpeedrunConfig;
import org.mcsr.speedrunapi.config.screen.SpeedrunConfigScreen;
import org.mcsr.speedrunapi.config.screen.widgets.option.NumberOptionSliderWidget;
import org.mcsr.speedrunapi.config.screen.widgets.option.NumberOptionTextFieldWidget;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Optional annotation for {@link SpeedrunConfig}'s to specify certain options before the config is instantiated.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Config {

    /**
     * @return When to create the {@link SpeedrunConfig} instance (default {@link InitPoint#ONINITIALIZE}).
     */
    InitPoint init() default InitPoint.ONINITIALIZE;

    /**
     * @return The priority the {@link SpeedrunConfig} should have during loading, higher priority will be loaded later (default {@code 1000}).
     */
    int priority() default 1000;

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

    /**
     * Sets the name used in the config screen for the annotated option.
     * <p>
     * If an option is not using this annotation, "speedrunapi.config.modid.option.theOption" will be used as the translation key instead.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Name {

        /**
         * @return Returns the translation key for the annotated option's name.
         */
        String value();
    }

    /**
     * Optional annotation that sets a text getter to customize the display of the option value.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Text {
        /**
         * The method needs to be declared in the same {@link SpeedrunConfig} class as the annotated option.
         * It has to take one paramter of the annotated option's type and returns {@link net.minecraft.text.Text}.
         *
         * @return Returns the name (not including parameters) of the Getter method that should be used for the annotated option.
         */
        String getter();
    }

    /**
     * Sets the description used in the config screen for the annotated option.
     * <p>
     * If an option is not using this annotation, "speedrunapi.config.modid.option.theOption.description" will be used as the translation key instead.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Description {

        /**
         * @return Returns the translation key for the annotated option's description.
         */
        String value();
    }

    /**
     * Sets the category used in the config screen for the annotated option.
     * <p>
     * The translation key used for the category name will be "speedrunapi.config.modid.category.theCategory".
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Category {

        /**
         * @return Returns the id of the config category.
         */
        String value();
    }

    /**
     * Hides an option from the config screen.
     * <p>
     * The field will not be available from the {@link SpeedrunConfigScreen}, but will be saved in the config file.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Hide {
    }

    /**
     * Optional annotation that sets Getter and/or Setter methods that will be used to access the annotated option instead of getting/setting it directly.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Access {

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

    /**
     * Annotation for fields in config classes that are not configurable.
     * <p>
     * The field will not be available from the {@link SpeedrunConfigScreen} and will not be saved in the config file.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Ignored {
    }

    class Numbers {

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
                EnforceBounds enforce() default EnforceBounds.TRUE;
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
                EnforceBounds enforce() default EnforceBounds.TRUE;
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

        public enum EnforceBounds {
            FALSE,
            MIN_ONLY,
            MAX_ONLY,
            TRUE;

            public boolean enforceMin() {
                return this == MIN_ONLY || this == TRUE;
            }

            public boolean enforceMax() {
                return this == MAX_ONLY || this == TRUE;
            }
        }
    }

    class Strings {

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
