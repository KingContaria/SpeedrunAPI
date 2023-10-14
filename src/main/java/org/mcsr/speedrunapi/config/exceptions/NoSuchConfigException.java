package org.mcsr.speedrunapi.config.exceptions;

public class NoSuchConfigException extends SpeedrunConfigAPIException {

    public NoSuchConfigException() {
    }

    public NoSuchConfigException(String message) {
        super(message);
    }

    public NoSuchConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchConfigException(Throwable cause) {
        super(cause);
    }
}
