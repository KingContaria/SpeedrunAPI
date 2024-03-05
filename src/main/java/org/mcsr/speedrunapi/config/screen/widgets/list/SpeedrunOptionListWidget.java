package org.mcsr.speedrunapi.config.screen.widgets.list;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Language;
import org.mcsr.speedrunapi.config.SpeedrunConfigContainer;
import org.mcsr.speedrunapi.config.api.SpeedrunOption;
import org.mcsr.speedrunapi.config.screen.SpeedrunConfigScreen;
import org.mcsr.speedrunapi.config.screen.widgets.TextWidget;

import java.util.*;

public class SpeedrunOptionListWidget extends ElementListWidget<SpeedrunOptionListWidget.OptionListEntry> {

    private final SpeedrunConfigScreen parent;

    public SpeedrunOptionListWidget(SpeedrunConfigContainer<?> config, SpeedrunConfigScreen parent, MinecraftClient client, int width, int height, int top, int bottom) {
        super(client, width, height, top, bottom, 30);
        this.parent = parent;

        Map<String, Set<SpeedrunOption<?>>> categorizedOptions = new LinkedHashMap<>();
        for (SpeedrunOption<?> option : config.getOptions()) {
            if (!option.hasWidget()) {
                continue;
            }
            if (option.getCategory() != null) {
                categorizedOptions.computeIfAbsent(option.getCategory(), string -> new LinkedHashSet<>()).add(option);
                continue;
            }
            this.addEntry(new OptionEntry(option));
        }
        for (Map.Entry<String, Set<SpeedrunOption<?>>> category : categorizedOptions.entrySet()) {
            String categoryTranslation = "speedrunapi.config." + config.getModContainer().getMetadata().getId() + ".category." + category.getKey();
            if (!Language.getInstance().hasTranslation(categoryTranslation) && Language.getInstance().hasTranslation(category.getKey())) {
                categoryTranslation = category.getKey();
            }
            this.addEntry(new OptionCategoryEntry(new TranslatableText(categoryTranslation)));
            for (SpeedrunOption<?> option : category.getValue()) {
                this.addEntry(new OptionEntry(option));
            }
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

    @Override
    protected void moveSelection(EntryListWidget.MoveDirection direction) {
        this.moveSelectionIf(direction, entry -> !(entry instanceof OptionCategoryEntry));
    }

    public abstract static class OptionListEntry extends ElementListWidget.Entry<OptionListEntry> {
    }

    public class OptionEntry extends OptionListEntry {

        private final TextWidget text;
        private final AbstractButtonWidget button;

        public OptionEntry(SpeedrunOption<?> option) {
            this.text = new TextWidget(SpeedrunOptionListWidget.this.parent, SpeedrunOptionListWidget.this.client.textRenderer, option.getName(), option.getDescription());
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
            return ImmutableList.of(this.text, this.button);
        }
    }

    public class OptionCategoryEntry extends OptionListEntry {

        private final Text category;

        public OptionCategoryEntry(Text category) {
            this.category = category;
        }

        @Override
        public List<? extends Element> children() {
            return ImmutableList.of();
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            SpeedrunOptionListWidget.this.drawCenteredText(matrices, SpeedrunOptionListWidget.this.client.textRenderer, this.category, x + entryWidth / 2, y + entryHeight / 2, 0xFFFFFF);
        }
    }
}


