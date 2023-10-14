package org.mcsr.speedrunapi.properties.exceptions;

public class NoSuchPropertyException extends SpeedrunPropertiesAPIException {
    public NoSuchPropertyException() {
    }

    public NoSuchPropertyException(String message) {
        super(message);
    }

    public NoSuchPropertyException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchPropertyException(Throwable cause) {
        super(cause);
    }
}
