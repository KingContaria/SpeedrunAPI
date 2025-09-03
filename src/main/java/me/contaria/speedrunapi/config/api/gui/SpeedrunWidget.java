package me.contaria.speedrunapi.config.api.gui;

public interface SpeedrunWidget {
    void render(int mouseX, int mouseY);

    int getX();

    void setX(int x);

    int getY();

    void setY(int y);

    int getWidth();

    int getHeight();

    default boolean keyPressed(char id, int code) {
        return false;
    }

    default boolean mouseClicked(int mouseX, int mouseY, int button) {
        return false;
    }

    default boolean mouseReleased(int mouseX, int mouseY, int button) {
        return false;
    }

    default boolean mouseDragged(int mouseX, int mouseY, int button, long mouseLastClicked) {
        return false;
    }

    default void tick() {
    }
}
