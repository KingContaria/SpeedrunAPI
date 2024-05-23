package org.mcsr.speedrunapi.config.screen.widgets.option;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.mcsr.speedrunapi.config.option.NumberOption;
import org.mcsr.speedrunapi.config.screen.widgets.IconButtonWidget;

@ApiStatus.Internal
public class NumberOptionTextFieldWidget<T extends NumberOption<?>> extends TextFieldWidget {

    private static final Identifier APPLYTEXTURE = new Identifier("textures/gui/container/beacon.png");
    private final NumberOption<?> option;
    private final ButtonWidget applyButton;

    public NumberOptionTextFieldWidget(T option, int x, int y) {
        super(MinecraftClient.getInstance().textRenderer, x, y, 125, 20, LiteralText.EMPTY);
        this.option = option;
        this.applyButton = new IconButtonWidget(APPLYTEXTURE, 90, 222, 256, 256, x + 130, y, button -> this.apply());
        this.updateText();
        this.setChangedListener(string -> this.applyButton.active = !this.option.get().toString().equals(string));
    }

    private void apply() {
        try {
            this.option.setFromString(this.getText());
            this.updateText();
        } catch (NumberFormatException e) {
            this.updateText();
        }
    }

    private void updateText() {
        this.setText(this.option.get().toString());
        this.applyButton.active = false;
    }

    @Override
    public int getWidth() {
        return super.getWidth() + 5 + this.applyButton.getWidth();
    }

    @Override
    public void setWidth(int value) {
        super.setWidth(value - 5 - this.applyButton.getWidth());
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        this.applyButton.x = this.x + 130;
        this.applyButton.y = this.y;
        this.applyButton.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button) || this.applyButton.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return super.mouseReleased(mouseX, mouseY, button) || this.applyButton.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY) || this.applyButton.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
}
