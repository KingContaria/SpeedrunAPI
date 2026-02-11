package me.contaria.speedrunapi.config.screen.widgets.option;

import me.contaria.speedrunapi.config.api.gui.SpeedrunWidget;
import me.contaria.speedrunapi.config.option.StringOption;
import me.contaria.speedrunapi.mixin.accessor.TextFieldWidgetAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class StringOptionTextFieldWidget implements SpeedrunWidget {
    private final StringOption option;
    private final TextFieldWidget textWidget;

    private int x;
    private int y;

    public StringOptionTextFieldWidget(StringOption option) {
        this.option = option;
        this.textWidget = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 150, 20);
        this.textWidget.setMaxLength(option.getMaxLength());
        this.textWidget.setText(option.get());
    }

    @Override
    public void render(int mouseX, int mouseY) {
        ((TextFieldWidgetAccessor) this.textWidget).speedrunapi$setX(this.x);
        ((TextFieldWidgetAccessor) this.textWidget).speedrunapi$setY(this.y);
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
        boolean bl = this.textWidget.keyPressed(id, code);
        this.option.set(this.textWidget.getText());
        return bl;
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        this.textWidget.mouseClicked(mouseX, mouseY, button);
        return true;
    }
}
