package me.contaria.speedrunapi.config.screen.widgets;

import me.contaria.speedrunapi.config.screen.AbstractSpeedrunConfigAPIScreen;
import net.minecraft.client.font.TextRenderer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@ApiStatus.Internal
public class TextWidget {
    private final AbstractSpeedrunConfigAPIScreen screen;
    private final TextRenderer textRenderer;
    @NotNull
    private final String text;
    @Nullable
    private final String tooltip;
    private final int minTooltipY;
    private final int maxTooltipY;

    public int x;
    public int y;

    public TextWidget(AbstractSpeedrunConfigAPIScreen screen, TextRenderer textRenderer, @NotNull String text) {
        this(screen, textRenderer, text, null);
    }

    public TextWidget(AbstractSpeedrunConfigAPIScreen screen, TextRenderer textRenderer, @NotNull String text, @Nullable String tooltip) {
        this(screen, textRenderer, text, tooltip, 0, screen.height);
    }

    public TextWidget(AbstractSpeedrunConfigAPIScreen screen, TextRenderer textRenderer, @NotNull String text, @Nullable String tooltip, int minTooltipY, int maxTooltipY) {
        this.screen = screen;
        this.textRenderer = textRenderer;
        this.text = text;
        this.tooltip = tooltip;
        this.minTooltipY = minTooltipY;
        this.maxTooltipY = maxTooltipY;
    }

    public void render(int mouseX, int mouseY) {
        this.renderText();
        this.renderTooltip(mouseX, mouseY);
    }

    public void renderText() {
        this.textRenderer.draw(this.text, this.x, this.y, 0xFFFFFF);
    }

    public void renderTooltip(int mouseX, int mouseY) {
        if (this.tooltip != null && this.isMouseOver(mouseX, mouseY)) {
            List<String> tooltip = this.textRenderer.wrapLines(this.tooltip, 200);
            int height = tooltip.size() * 10;
            int y = mouseY;
            y = Math.min(y, this.maxTooltipY - height);
            y = Math.max(y, this.minTooltipY - height);
            this.screen.setTooltip(tooltip, mouseX, y);
        }
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX > this.x && mouseX < this.x + this.textRenderer.getStringWidth(this.text) && mouseY > this.y && mouseY < this.y + this.textRenderer.fontHeight;
    }

    public int getWidth() {
        return this.textRenderer.getStringWidth(this.text);
    }
}
