package me.contaria.speedrunapi.config.screen.widgets.option;

import me.contaria.speedrunapi.config.option.NumberOption;
import me.contaria.speedrunapi.config.screen.widgets.IconButtonWidget;
import me.contaria.speedrunapi.util.IdentifierUtil;
import me.contaria.speedrunapi.util.TextUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class NumberOptionTextFieldWidget<T extends NumberOption<?>> extends TextFieldWidget {
    private static final Identifier APPLY_TEXTURE = IdentifierUtil.ofVanilla("textures/gui/container/beacon.png");

    private final NumberOption<?> option;
    private final ButtonWidget applyButton;

    public NumberOptionTextFieldWidget(T option, int x, int y) {
        super(MinecraftClient.getInstance().textRenderer, x, y, 125, 20, TextUtil.empty());
        this.option = option;
        this.applyButton = new IconButtonWidget(APPLY_TEXTURE, 90, 222, 256, 256, x + 130, y, button -> this.apply());
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
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.applyButton.setX(this.getX() + 130);
        this.applyButton.setY(this.getY());
        this.applyButton.render(context, mouseX, mouseY, delta);
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
