package org.mcsr.speedrunapi.config.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class Config {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Name {

        String value();

        boolean literal() default false;

        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        @interface Auto {
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Description {

        String value();

        boolean literal() default false;

        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        @interface Auto {
        }
    }

    public static class Numbers {

        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        public @interface FractionalBounds {

            double min() default 0.0;

            double max();
        }

        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        public @interface WholeBounds {

            long min() default 0L;

            long max();
        }
    }

    public static class Strings {

        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        public @interface MaxChars {

            /**
             * @return Max Characters the String option is allowed to be.
             */
            int value() default 100;
        }
    }
}
