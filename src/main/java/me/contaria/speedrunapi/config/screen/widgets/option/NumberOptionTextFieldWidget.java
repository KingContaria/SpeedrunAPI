package me.contaria.speedrunapi.config.screen.widgets.option;

import me.contaria.speedrunapi.config.api.gui.CallbackButtonWidget;
import me.contaria.speedrunapi.config.api.gui.SpeedrunWidget;
import me.contaria.speedrunapi.config.option.NumberOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PagedEntryListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class NumberOptionTextFieldWidget<T extends NumberOption<?>> implements SpeedrunWidget {
    private final NumberOption<?> option;
    private final TextFieldWidget textWidget;
    private final ButtonWidget applyButton;

    private int x;
    private int y;

    public NumberOptionTextFieldWidget(T option) {
        super();
        this.option = option;
        this.textWidget = new TextFieldWidget(-1, MinecraftClient.getInstance().textRenderer, 0, 0, 125, 20);
        this.applyButton = new CallbackButtonWidget(20, 20, Formatting.BOLD + "✓", button -> this.apply());
        this.updateText();
        this.textWidget.setListener(new Listener(option));
    }

    private void apply() {
        try {
            this.option.setFromString(this.textWidget.getText());
            this.updateText();
        } catch (NumberFormatException e) {
            this.updateText();
        }
    }

    private void updateText() {
        this.textWidget.setText(this.option.get().toString());
        this.applyButton.active = false;
    }

    @Override
    public void render(int mouseX, int mouseY) {
        this.textWidget.x = this.x;
        this.textWidget.y = this.y;
        this.textWidget.render();

        this.applyButton.x = this.x + 130;
        this.applyButton.y = this.y;
        this.applyButton.render(MinecraftClient.getInstance(), mouseX, mouseY);
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
        if (this.applyButton.isMouseOver(MinecraftClient.getInstance(), mouseX, mouseY)) {
            this.apply();
            return true;
        }
        this.textWidget.mouseClicked(mouseX, mouseY, button);
        return true;
    }

    private class Listener implements PagedEntryListWidget.Listener {
        private final NumberOption<?> option;

        private Listener(NumberOption<?> option) {
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
            NumberOptionTextFieldWidget.this.applyButton.active = !this.option.get().toString().equals(text);
        }
    }
}
