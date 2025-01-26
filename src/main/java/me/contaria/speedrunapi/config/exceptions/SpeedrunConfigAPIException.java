package me.contaria.speedrunapi.config.exceptions;

public class SpeedrunConfigAPIException extends RuntimeException {
    public SpeedrunConfigAPIException() {
    }

    public SpeedrunConfigAPIException(String message) {
        super(message);
    }

    public SpeedrunConfigAPIException(String message, Throwable cause) {
        super(message, cause);
    }

    public SpeedrunConfigAPIException(Throwable cause) {
        super(cause);
    }
}
