package org.mcsr.speedrunapi.config.exceptions;

public class UnsupportedConfigException extends SpeedrunConfigAPIException {

    public UnsupportedConfigException() {
    }

    public UnsupportedConfigException(String message) {
        super(message);
    }

    public UnsupportedConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedConfigException(Throwable cause) {
        super(cause);
    }
}
