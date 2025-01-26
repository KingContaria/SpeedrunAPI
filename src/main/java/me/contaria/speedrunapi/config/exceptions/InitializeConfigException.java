package me.contaria.speedrunapi.config.exceptions;

public class InitializeConfigException extends SpeedrunConfigAPIException {
    public InitializeConfigException() {
    }

    public InitializeConfigException(String message) {
        super(message);
    }

    public InitializeConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public InitializeConfigException(Throwable cause) {
        super(cause);
    }
}
