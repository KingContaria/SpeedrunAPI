package me.contaria.speedrunapi.config.screen.widgets;

import me.contaria.speedrunapi.mixin.accessor.ScreenAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@ApiStatus.Internal
public class TextWidget implements Renderable, GuiEventListener {
    private final Screen screen;
    private final Font textRenderer;
    @NotNull
    private final Component text;
    @Nullable
    private final Component tooltip;
    private final int minTooltipY;
    private final int maxTooltipY;

    public int x;
    public int y;

    public TextWidget(Screen screen, Font textRenderer, @NotNull Component text) {
        this(screen, textRenderer, text, null);
    }

    public TextWidget(Screen screen, Font textRenderer, @NotNull Component text, @Nullable Component tooltip) {
        this(screen, textRenderer, text, tooltip, 0, screen.height);
    }

    public TextWidget(Screen screen, Font textRenderer, @NotNull Component text, @Nullable Component tooltip, int minTooltipY, int maxTooltipY) {
        this.screen = screen;
        this.textRenderer = textRenderer;
        this.text = text;
        this.tooltip = tooltip;
        this.minTooltipY = minTooltipY;
        this.maxTooltipY = maxTooltipY;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0) {
            ActiveTextCollector.ClickableStyleFinder finder = new ActiveTextCollector.ClickableStyleFinder(this.textRenderer, (int)event.x(), (int)event.y());
            finder.accept(x, y, text.getVisualOrderText());
            Style clicked = finder.result();
            if (clicked != null) {
                ScreenAccessor.callDefaultHandleClickEvent(clicked.getClickEvent(), Minecraft.getInstance(), this.screen);
                return true;
            }
        }
        return false;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        this.renderText(graphics);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    public void renderText(GuiGraphicsExtractor graphics) {
        graphics.text(this.textRenderer, this.text.copy(), this.x, this.y, 0xFFFFFFff, false);
    }

    public void renderTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (this.tooltip != null && this.isMouseOver(mouseX, mouseY)) {
            List<FormattedCharSequence> tooltip = this.textRenderer.split(this.tooltip, 200);
            int height = tooltip.size() * 10;
            int y = mouseY;
            y = Math.min(y, this.maxTooltipY - height);
            y = Math.max(y, this.minTooltipY - height);
            graphics.setTooltipForNextFrame(this.textRenderer, tooltip, mouseX, y);
        }
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX > this.x && mouseX < this.x + this.textRenderer.width(this.text) && mouseY > this.y && mouseY < this.y + this.textRenderer.lineHeight;
    }

    @Override
    public void setFocused(boolean focused) {
    }

    @Override
    public boolean isFocused() {
        return false;
    }

    public Component getTextComponentAtPosition(double x, double y) {
        if (this.isMouseOver(x, y)) {
            return this.getTextComponentAtPositionInternal(this.text, 0, this.textRenderer.width(this.text.plainCopy()), x, y);
        }
        return null;
    }

    private Component getTextComponentAtPositionInternal(Component text, int textX, int width, double x, double y) {
        if (x > this.x + textX && x < this.x + textX + width && y > this.y && y < this.y + this.textRenderer.lineHeight) {
            return text;
        }
        textX += width;
        for (Component sibling : text.getSiblings()) {
            int siblingWidth = this.textRenderer.width(sibling.plainCopy());
            Component textAtPosition = this.getTextComponentAtPositionInternal(sibling, textX, siblingWidth, x, y);
            if (textAtPosition != null) {
                return textAtPosition;
            }
            textX += siblingWidth;
        }
        return null;
    }

    public int getWidth() {
        return this.textRenderer.width(this.text);
    }
}
