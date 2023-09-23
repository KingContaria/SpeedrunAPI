package org.mcsr.speedrunapi.config.screen.widgets.list;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.util.math.MatrixStack;
import org.mcsr.speedrunapi.config.SpeedrunConfigContainer;
import org.mcsr.speedrunapi.config.option.Option;
import org.mcsr.speedrunapi.config.screen.SpeedrunConfigScreen;
import org.mcsr.speedrunapi.config.screen.widgets.option.OptionTextWidget;

import java.util.ArrayList;
import java.util.List;

public class SpeedrunOptionListWidget extends ElementListWidget<SpeedrunOptionListWidget.OptionEntry> {

    private final SpeedrunConfigScreen parent;
    private final SpeedrunConfigContainer<?> config;

    public SpeedrunOptionListWidget(SpeedrunConfigContainer<?> config, SpeedrunConfigScreen parent, MinecraftClient client, int width, int height, int top, int bottom) {
        super(client, width, height, top, bottom, 30);
        this.parent = parent;
        this.config = config;

        for (Option<?> option : config.getOptions()) {
            this.addEntry(new OptionEntry(option));
        }
    }

    @Override
    public int getRowWidth() {
        return 300;
    }

    @Override
    protected int getScrollbarPositionX() {
        return super.getScrollbarPositionX() + 32;
    }

    public class OptionEntry extends ElementListWidget.Entry<OptionEntry> {

        private final OptionTextWidget<?> text;
        private final AbstractButtonWidget button;

        public OptionEntry(Option<?> option) {
            this.text = new OptionTextWidget<>(SpeedrunOptionListWidget.this.parent, SpeedrunOptionListWidget.this.client.textRenderer, option);
            this.button = option.createWidget();
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.text.x = x + 5;
            int y_offset = (20 - SpeedrunOptionListWidget.this.client.textRenderer.fontHeight) / 2;
            this.text.y = y + 5 + y_offset;
            this.text.render(matrices, mouseX, mouseY, tickDelta);

            this.button.x = x + entryWidth - this.button.getWidth() - 5;
            this.button.y = y + 5;
            this.button.render(matrices, mouseX, mouseY, tickDelta);
        }

        @Override
        public List<? extends Element> children() {
            List<Element> children = new ArrayList<>();
            children.add(this.text);
            children.add(this.button);
            return children;
        }
    }
}


