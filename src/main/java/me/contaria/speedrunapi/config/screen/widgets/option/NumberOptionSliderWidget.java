package me.contaria.speedrunapi.config.screen.widgets.option;

import me.contaria.speedrunapi.config.option.NumberOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.PagedEntryListWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class NumberOptionSliderWidget<T extends NumberOption<?>> extends SliderWidget {
    private final T option;

    public NumberOptionSliderWidget(T option) {
        super(new PagedEntryListWidget.Listener() {
            @Override
            public void setBooleanValue(int id, boolean value) {
            }

            @Override
            public void setFloatValue(int id, float value) {
            }

            @Override
            public void setStringValue(int id, String text) {
            }
        }, -1, 0, 0, "UNUSED TRANSLATION KEY", option.getSliderMin(), option.getSliderMax(), option.get().floatValue(), (id, name, value) -> option.getText());
        this.option = option;
        this.setSliderProgress((float) ((option.get().doubleValue() - option.getSliderMin()) / (option.getSliderMax() - option.getSliderMin())));
    }

    private void updateValue() {
        this.option.setFromSliderValue(this.getProgress());
        this.message = this.option.getText();
    }

    @Override
    public void setSliderValue(float value, boolean updateListener) {
        super.setSliderValue(value, updateListener);
        this.updateValue();
    }

    @Override
    protected void mouseDragged(MinecraftClient client, int mouseX, int mouseY) {
        super.mouseDragged(client, mouseX, mouseY);
        this.updateValue();
    }

    @Override
    public void setSliderProgress(float progress) {
        super.setSliderProgress(progress);
        this.updateValue();
    }

    @Override
    public boolean isMouseOver(MinecraftClient client, int mouseX, int mouseY) {
        boolean isMouseOver = super.isMouseOver(client, mouseX, mouseY);
        this.updateValue();
        return isMouseOver;
    }
}
