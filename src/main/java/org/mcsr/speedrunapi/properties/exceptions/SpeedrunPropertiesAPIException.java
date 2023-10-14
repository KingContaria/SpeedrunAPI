package org.mcsr.speedrunapi.properties.exceptions;

public class SpeedrunPropertiesAPIException extends RuntimeException {

    public SpeedrunPropertiesAPIException() {
    }

    public SpeedrunPropertiesAPIException(String message) {
        super(message);
    }

    public SpeedrunPropertiesAPIException(String message, Throwable cause) {
        super(message, cause);
    }

    public SpeedrunPropertiesAPIException(Throwable cause) {
        super(cause);
    }
}
