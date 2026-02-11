package me.contaria.speedrunapi.config.screen.widgets.option;

import me.contaria.speedrunapi.config.option.NumberOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.opengl.GL11;

@ApiStatus.Internal
public class NumberOptionSliderWidget<T extends NumberOption<?>> extends ButtonWidget {
    private final T option;

    private double progress;
    public boolean focused;

    public NumberOptionSliderWidget(T option) {
        super(-1, 0, 0, 150, 20, option.getText());
        this.option = option;
        this.progress = (option.get().doubleValue() - option.getSliderMin()) / (option.getSliderMax() - option.getSliderMin());
    }

    @Override
    public int getYImage(boolean isHovered) {
        return 0;
    }

    @Override
    protected void mouseDragged(MinecraftClient client, int mouseX, int mouseY) {
        if (this.visible) {
            if (this.focused) {
                this.progress = Math.max(0.0, Math.min(1.0, (mouseX - (this.x + 4.0)) / (this.width - 8.0)));
                this.updateValue();
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexture(this.x + (int)(this.progress * (this.width - 8)), this.y, 0, 66, 4, 20);
            this.drawTexture(this.x + (int)(this.progress * (this.width - 8)) + 4, this.y, 196, 66, 4, 20);
        }
    }

    @Override
    public boolean isMouseOver(MinecraftClient client, int mouseX, int mouseY) {
        if (super.isMouseOver(client, mouseX, mouseY)) {
            this.progress = Math.max(0.0, Math.min(1.0, (mouseX - (this.x + 4.0)) / (this.width - 8.0)));
            this.updateValue();
            this.focused = true;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        this.focused = false;
    }

    private void updateValue() {
        this.option.setFromSliderValue(this.progress);
        this.message = this.option.getText();
    }
}
