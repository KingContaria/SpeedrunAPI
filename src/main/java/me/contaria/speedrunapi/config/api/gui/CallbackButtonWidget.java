package me.contaria.speedrunapi.config.api.gui;

import net.minecraft.client.gui.widget.ButtonWidget;

import java.util.function.Consumer;

/**
 * Provides a simple implementation of a {@link ButtonWidget} implementing a {@link ButtonWidgetCallback}.
 */
public class CallbackButtonWidget extends ButtonWidget implements ButtonWidgetCallback {
    private final Consumer<CallbackButtonWidget> callback;

    public CallbackButtonWidget(String message, Consumer<CallbackButtonWidget> callback) {
        this(-1, 0, 0, 150, 20, message, callback);
    }

    public CallbackButtonWidget(int width, int height, String message, Consumer<CallbackButtonWidget> callback) {
        this(-1, 0, 0, width, height, message, callback);
    }

    public CallbackButtonWidget(int x, int y, int width, int height, String message, Consumer<CallbackButtonWidget> callback) {
        this(-1, x, y, width, height, message, callback);
    }

    public CallbackButtonWidget(int id, int width, int height, String message, Consumer<CallbackButtonWidget> callback) {
        this(id, 0, 0, width, height, message, callback);
    }

    public CallbackButtonWidget(int id, int x, int y, int width, int height, String message, Consumer<CallbackButtonWidget> callback) {
        super(id, x, y, width, height, message);
        this.callback = callback;
    }

    @Override
    public void onPress() {
        this.callback.accept(this);
    }
}
