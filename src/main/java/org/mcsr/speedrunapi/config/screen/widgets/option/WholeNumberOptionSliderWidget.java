package org.mcsr.speedrunapi.config.screen.widgets.option;

import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;
import org.mcsr.speedrunapi.config.option.WholeNumberOption;

public class WholeNumberOptionSliderWidget<T extends Number> extends NumberOptionSliderWidget<WholeNumberOption<T>> {

    public WholeNumberOptionSliderWidget(WholeNumberOption<T> option, int x, int y) {
        super(option, x, y, (option.get().doubleValue() - option.getMin()) / (option.getMax() - option.getMin()));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_RIGHT) {
            this.option.setLong(this.option.get().longValue() + (keyCode == GLFW.GLFW_KEY_LEFT ? -1 : 1));
            return true;
        }
        return false;
    }

    @Override
    protected void updateMessage() {
        this.setMessage(new LiteralText(this.option.get().toString()));
    }

    @Override
    protected void applyValue() {
        this.option.setFromSliderValue(this.value);
    }
}
