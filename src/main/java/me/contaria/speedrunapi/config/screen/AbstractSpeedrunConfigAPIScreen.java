package me.contaria.speedrunapi.config.screen;

import me.contaria.speedrunapi.config.api.gui.ButtonWidgetCallback;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AbstractSpeedrunConfigAPIScreen extends Screen {
    @Nullable
    private List<String> tooltip;
    private int tooltipX;
    private int tooltipY;

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if (button instanceof ButtonWidgetCallback) {
            ((ButtonWidgetCallback) button).onPress();
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {
        super.render(mouseX, mouseY, tickDelta);
        if (this.tooltip != null) {
            this.renderTooltip(this.tooltip, this.tooltipX, this.tooltipY);
            this.tooltip = null;
        }
    }

    public void setTooltip(List<String> tooltip, int x, int y) {
        this.tooltip = tooltip;
        this.tooltipX = x;
        this.tooltipY = y;
    }
}
