package me.contaria.speedrunapi.config.screen.widgets.list;

import com.google.common.collect.ImmutableList;
import me.contaria.speedrunapi.config.SpeedrunConfigContainer;
import me.contaria.speedrunapi.config.api.SpeedrunOption;
import me.contaria.speedrunapi.config.screen.SpeedrunConfigScreen;
import me.contaria.speedrunapi.config.screen.widgets.TextWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Language;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@ApiStatus.Internal
public class SpeedrunOptionListWidget extends ElementListWidget<SpeedrunOptionListWidget.OptionListEntry> {
    private final SpeedrunConfigScreen parent;
    private final SpeedrunConfigContainer<?> config;

    @Nullable
    private TextWidget tooltipToRender;

    public SpeedrunOptionListWidget(SpeedrunConfigScreen parent, SpeedrunConfigContainer<?> config, MinecraftClient client, int width, int height, int top, int bottom, String filter) {
        super(client, width, height, top, bottom, 30);
        this.parent = parent;
        this.config = config;
        this.updateEntries(filter);
    }

    public void updateEntries(String filter) {
        this.clearEntries();

        filter = filter.toLowerCase(Locale.ENGLISH);

        Map<String, Set<SpeedrunOption<?>>> categorizedOptions = new LinkedHashMap<>();
        for (SpeedrunOption<?> option : this.config.getOptions()) {
            if (!filter.isEmpty() && !option.getName().toLowerCase(Locale.ENGLISH).contains(filter)) {
                continue;
            }
            if (!option.hasWidget() || !this.config.getConfig().shouldShowOption(option.getID())) {
                continue;
            }
            if (option.getCategory() != null) {
                categorizedOptions.computeIfAbsent(option.getCategory(), string -> new LinkedHashSet<>()).add(option);
                continue;
            }
            this.addEntry(new OptionEntry(option));
        }

        for (Map.Entry<String, Set<SpeedrunOption<?>>> category : categorizedOptions.entrySet()) {
            if (!this.config.getConfig().shouldShowCategory(category.getKey())) {
                continue;
            }
            String categoryTranslation = "speedrunapi.config." + this.config.getModContainer().getMetadata().getId() + ".category." + category.getKey();
            if (!Language.getInstance().hasTranslation(categoryTranslation) && Language.getInstance().hasTranslation(category.getKey())) {
                categoryTranslation = category.getKey();
            }
            this.addEntry(new OptionCategoryEntry(I18n.translate(categoryTranslation)));
            for (SpeedrunOption<?> option : category.getValue()) {
                this.addEntry(new OptionEntry(option));
            }
        }

        this.setScrollAmount(0.0);
    }

    public void adjustTop(int top) {
        this.top = top;
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        super.render(mouseX, mouseY, delta);
        if (this.tooltipToRender != null) {
            this.tooltipToRender.renderTooltip(mouseX, mouseY);
            this.tooltipToRender = null;
        }
    }

    @Override
    public int getRowWidth() {
        return Math.min(this.width, 320);
    }

    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 42;
    }
/*
    @Override
    protected void moveSelection(EntryListWidget.MoveDirection direction) {
        this.moveSelectionIf(direction, entry -> !(entry instanceof OptionCategoryEntry));
    }

 */

    public abstract static class OptionListEntry extends ElementListWidget.Entry<OptionListEntry> {
    }

    public class OptionEntry extends OptionListEntry {
        private final TextWidget text;
        private final AbstractButtonWidget button;

        public OptionEntry(SpeedrunOption<?> option) {
            this.text = new TextWidget(SpeedrunOptionListWidget.this.parent, SpeedrunOptionListWidget.this.minecraft.textRenderer, option.getName(), option.getDescription(), SpeedrunOptionListWidget.this.top, SpeedrunOptionListWidget.this.bottom);
            this.button = option.createWidget();
        }

        @Override
        public void render(int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.text.x = x + 5;
            int y_offset = (20 - SpeedrunOptionListWidget.this.minecraft.textRenderer.fontHeight) / 2;
            this.text.y = y + 5 + y_offset;
            this.text.renderText();

            this.button.x = x + entryWidth - this.button.getWidth() - 5;
            this.button.y = y + 5;
            this.button.render(mouseX, mouseY, tickDelta);

            if (this.isMouseOver(mouseX, mouseY) && this.text.isMouseOver(mouseX, mouseY)) {
                SpeedrunOptionListWidget.this.tooltipToRender = this.text;
            }
        }

        @Override
        public List<? extends Element> children() {
            return ImmutableList.of(this.text, this.button);
        }
    }

    public class OptionCategoryEntry extends OptionListEntry {

        private final String category;

        public OptionCategoryEntry(String category) {
            this.category = category;
        }

        @Override
        public List<? extends Element> children() {
            return ImmutableList.of();
        }

        @Override
        public void render(int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            SpeedrunOptionListWidget.this.drawCenteredString(SpeedrunOptionListWidget.this.minecraft.textRenderer, this.category, x + entryWidth / 2, y + entryHeight / 2, 0xFFFFFF);
        }
    }
}


