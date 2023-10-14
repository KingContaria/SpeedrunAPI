package org.mcsr.speedrunapi.config.screen.widgets.option;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mcsr.speedrunapi.config.option.Option;

public class OptionTextWidget<T> implements Drawable, Element {

    private final Screen screen;
    private final TextRenderer textRenderer;
    @NotNull
    private final Text name;
    @Nullable
    private final Text description;
    public int x;
    public int y;

    public OptionTextWidget(Screen screen, TextRenderer textRenderer, Option<T> option) {
        this.screen = screen;
        this.textRenderer = textRenderer;
        this.name = option.getName();
        this.description = option.getDescription();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.textRenderer.draw(matrices, this.name, this.x, this.y, 0xFFFFFF);
        if (this.description != null && this.isMouseOver(mouseX, mouseY)) {
            this.screen.renderTooltip(matrices, this.textRenderer.wrapLines(this.description, 200), mouseX, mouseY);
        }
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX > this.x && mouseX < this.x + this.textRenderer.getWidth(this.name) && mouseY > this.y && mouseY < this.y + this.textRenderer.fontHeight;
    }
}
