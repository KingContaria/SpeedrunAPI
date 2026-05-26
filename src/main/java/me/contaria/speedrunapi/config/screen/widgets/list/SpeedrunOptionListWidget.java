package me.contaria.speedrunapi.config.screen.widgets.list;

import com.google.common.collect.ImmutableList;
import me.contaria.speedrunapi.config.SpeedrunConfigContainer;
import me.contaria.speedrunapi.config.api.SpeedrunOption;
import me.contaria.speedrunapi.config.screen.SpeedrunConfigScreen;
import me.contaria.speedrunapi.config.screen.widgets.TextWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@ApiStatus.Internal
public class SpeedrunOptionListWidget extends ContainerObjectSelectionList<SpeedrunOptionListWidget.OptionListEntry> {
    private final SpeedrunConfigScreen parent;
    private final SpeedrunConfigContainer<?> config;

    @Nullable
    private TextWidget tooltipToRender;

    public SpeedrunOptionListWidget(SpeedrunConfigScreen parent, SpeedrunConfigContainer<?> config, Minecraft client, int width, int height, int y, String filter) {
        super(client, width, height, y, 30);
        this.parent = parent;
        this.config = config;
        this.updateEntries(filter);
    }

    public void updateEntries(String filter) {
        this.clearEntries();

        filter = filter.toLowerCase(Locale.ENGLISH);

        Map<String, Set<SpeedrunOption<?>>> categorizedOptions = new LinkedHashMap<>();
        for (SpeedrunOption<?> option : this.config.getOptions()) {
            if (!filter.isEmpty() && !option.getName().getString().toLowerCase(Locale.ENGLISH).contains(filter)) {
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
            if (!Language.getInstance().has(categoryTranslation) && Language.getInstance().has(category.getKey())) {
                categoryTranslation = category.getKey();
            }
            this.addEntry(new OptionCategoryEntry(Component.translatable(categoryTranslation)));
            for (SpeedrunOption<?> option : category.getValue()) {
                this.addEntry(new OptionEntry(option));
            }
        }

        this.setScrollAmount(0.0);
    }

    @Override
    protected void extractListItems(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractListItems(graphics, mouseX, mouseY, a);
        if (this.tooltipToRender != null) {
            this.tooltipToRender.renderTooltip(graphics, mouseX, mouseY);
            this.tooltipToRender = null;
        }
    }

    @Override
    public int getRowWidth() {
        return Math.min(this.width, 320);
    }

/*
    @Override
    protected void moveSelection(EntryListWidget.MoveDirection direction) {
        this.moveSelectionIf(direction, entry -> !(entry instanceof OptionCategoryEntry));
    }

 */

    public abstract static class OptionListEntry extends ContainerObjectSelectionList.Entry<OptionListEntry> {
    }

    public class OptionEntry extends OptionListEntry {

        private final TextWidget text;
        private final AbstractWidget button;

        public OptionEntry(SpeedrunOption<?> option) {
            this.text = new TextWidget(SpeedrunOptionListWidget.this.parent, SpeedrunOptionListWidget.this.minecraft.font, option.getName(), option.getDescription(), SpeedrunOptionListWidget.this.getY(), SpeedrunOptionListWidget.this.getBottom());
            this.button = option.createWidget();
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.text.x = this.getContentX() + 5;
            int y_offset = (20 - SpeedrunOptionListWidget.this.minecraft.font.lineHeight) / 2;
            this.text.y = this.getContentY() + 5 + y_offset;
            this.text.renderText(graphics);

            this.button.setX(this.getContentX() + this.getContentWidth() - this.button.getWidth() - 5);
            this.button.setY(this.getContentY() + 5);
            this.button.extractRenderState(graphics, mouseX, mouseY, tickDelta);

            if (this.isMouseOver(mouseX, mouseY) && this.text.isMouseOver(mouseX, mouseY)) {
                SpeedrunOptionListWidget.this.tooltipToRender = this.text;
            }
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of(this.button);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(this.button);
        }

        @Override
        public void setFocused(@Nullable final GuiEventListener focused) {
            if (this.getFocused() != focused) {
                super.setFocused(focused);
            }
        }
    }

    public class OptionCategoryEntry extends OptionListEntry {

        private final Component category;

        public OptionCategoryEntry(Component category) {
            this.category = category;
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
            graphics.centeredText(SpeedrunOptionListWidget.this.minecraft.font, this.category, this.getContentX() + this.getContentWidth() / 2, this.getContentY() + this.getContentHeight() / 2, 0xFFFFFFff);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of();
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of();
        }
    }
}


