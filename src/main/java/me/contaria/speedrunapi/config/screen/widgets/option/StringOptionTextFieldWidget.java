package me.contaria.speedrunapi.config.screen.widgets.option;

import me.contaria.speedrunapi.config.api.gui.SpeedrunWidget;
import me.contaria.speedrunapi.config.option.StringOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.PagedEntryListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class StringOptionTextFieldWidget implements SpeedrunWidget {
    private final TextFieldWidget textWidget;

    private int x;
    private int y;

    public StringOptionTextFieldWidget(StringOption option) {
        this.textWidget = new TextFieldWidget(-1, MinecraftClient.getInstance().textRenderer, 0, 0, 150, 20);
        this.textWidget.setMaxLength(option.getMaxLength());
        this.textWidget.setText(option.get());
        this.textWidget.setListener(new Listener(option));
    }

    @Override
    public void render(int mouseX, int mouseY) {
        this.textWidget.x = this.x;
        this.textWidget.y = this.y;
        this.textWidget.render();
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int getWidth() {
        return 150;
    }

    @Override
    public int getHeight() {
        return 20;
    }

    @Override
    public boolean keyPressed(char id, int code) {
        return this.textWidget.keyPressed(id, code);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        this.textWidget.mouseClicked(mouseX, mouseY, button);
        return true;
    }

    private static class Listener implements PagedEntryListWidget.Listener {
        private final StringOption option;

        private Listener(StringOption option) {
            this.option = option;
        }

        @Override
        public void setBooleanValue(int id, boolean value) {
        }

        @Override
        public void setFloatValue(int id, float value) {
        }

        @Override
        public void setStringValue(int id, String text) {
            this.option.set(text);
        }
    }
}
