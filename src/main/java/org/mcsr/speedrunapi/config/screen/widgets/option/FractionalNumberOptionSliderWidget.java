package org.mcsr.speedrunapi.config.screen.widgets.option;

import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;
import org.mcsr.speedrunapi.config.option.FractionalNumberOption;

public class FractionalNumberOptionSliderWidget<T extends Number> extends NumberOptionSliderWidget<FractionalNumberOption<T>> {

    public FractionalNumberOptionSliderWidget(FractionalNumberOption<T> option, int x, int y) {
        super(option, x, y, 0);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_RIGHT) {
            double interval = this.option.getIntervals();
            interval = interval != 0 ? interval : 1;
            this.option.setDouble(this.option.get().longValue() + (keyCode == GLFW.GLFW_KEY_LEFT ? -interval : interval));
            this.updateMessage();
            return true;
        }
        return false;
    }

    @Override
    protected void updateMessage() {
        this.setMessage(new LiteralText(String.valueOf(Math.round(this.option.get().doubleValue() * 100.0) / 100.0)));
    }

    @Override
    protected void applyValue() {
        this.option.setFromSliderValue(this.value);
    }

    @Override
    protected void updateValue() {
        this.value = ((double) this.option.get() - this.option.getMin()) / (this.option.getMax() - this.option.getMin());
        this.updateMessage();
    }
}
