package org.mcsr.speedrunapi.config.screen.widgets.option;

import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.glfw.GLFW;
import org.mcsr.speedrunapi.config.option.FractionalNumberOption;

@ApiStatus.Internal
public class FractionalNumberOptionSliderWidget<T extends Number> extends NumberOptionSliderWidget<FractionalNumberOption<T>> {

    public FractionalNumberOptionSliderWidget(FractionalNumberOption<T> option, int x, int y) {
        super(option, x, y, 0);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_RIGHT) {
            double interval = this.option.getIntervals();
            if (interval == 0) {
                return super.keyPressed(keyCode, scanCode, modifiers);
            }
            this.option.setDouble(this.option.get().doubleValue() + (keyCode == GLFW.GLFW_KEY_LEFT ? -interval : interval));
            this.updateValue();
            return true;
        }
        return false;
    }

    @Override
    protected void updateValue() {
        this.value = (this.option.get().doubleValue() - this.option.getMin()) / (this.option.getMax() - this.option.getMin());
        this.updateMessage();
    }
}
