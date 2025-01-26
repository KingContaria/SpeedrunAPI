package me.contaria.speedrunapi.config.exceptions;

public class ReflectionConfigException extends SpeedrunConfigAPIException {
    public ReflectionConfigException() {
    }

    public ReflectionConfigException(String message) {
        super(message);
    }

    public ReflectionConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReflectionConfigException(Throwable cause) {
        super(cause);
    }
}
