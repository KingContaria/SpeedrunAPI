package me.contaria.speedrunapi.config.screen.widgets.list;

import com.google.common.collect.ImmutableList;
import me.contaria.speedrunapi.config.SpeedrunConfigContainer;
import me.contaria.speedrunapi.config.api.SpeedrunOption;
import me.contaria.speedrunapi.config.screen.SpeedrunConfigScreen;
import me.contaria.speedrunapi.config.screen.widgets.TextWidget;
import me.contaria.speedrunapi.util.TextUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.text.Text;
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

    public SpeedrunOptionListWidget(SpeedrunConfigScreen parent, SpeedrunConfigContainer<?> config, MinecraftClient client, int width, int height, int y, String filter) {
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
            if (!Language.getInstance().hasTranslation(categoryTranslation) && Language.getInstance().hasTranslation(category.getKey())) {
                categoryTranslation = category.getKey();
            }
            this.addEntry(new OptionCategoryEntry(TextUtil.translatable(categoryTranslation)));
            for (SpeedrunOption<?> option : category.getValue()) {
                this.addEntry(new OptionEntry(option));
            }
        }

        this.setScrollAmount(0.0);
    }

    @Override
    protected void renderList(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderList(context, mouseX, mouseY, delta);
        if (this.tooltipToRender != null) {
            this.tooltipToRender.renderTooltip(context, mouseX, mouseY);
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

    public abstract static class OptionListEntry extends ElementListWidget.Entry<OptionListEntry> {
    }

    public class OptionEntry extends OptionListEntry {

        private final TextWidget text;
        private final ClickableWidget button;

        public OptionEntry(SpeedrunOption<?> option) {
            this.text = new TextWidget(SpeedrunOptionListWidget.this.parent, SpeedrunOptionListWidget.this.client.textRenderer, option.getName(), option.getDescription(), SpeedrunOptionListWidget.this.getY(), SpeedrunOptionListWidget.this.getBottom());
            this.button = option.createWidget();
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.text.x = x + 5;
            int y_offset = (20 - SpeedrunOptionListWidget.this.client.textRenderer.fontHeight) / 2;
            this.text.y = y + 5 + y_offset;
            this.text.renderText(context);

            this.button.setX(x + entryWidth - this.button.getWidth() - 5);
            this.button.setY(y + 5);
            this.button.render(context, mouseX, mouseY, tickDelta);

            if (this.isMouseOver(mouseX, mouseY) && this.text.isMouseOver(mouseX, mouseY)) {
                SpeedrunOptionListWidget.this.tooltipToRender = this.text;
            }
        }

        @Override
        public List<? extends Element> children() {
            return ImmutableList.of(this.text, this.button);
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return ImmutableList.of(this.button);
        }
    }

    public class OptionCategoryEntry extends OptionListEntry {

        private final Text category;

        public OptionCategoryEntry(Text category) {
            this.category = category;
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            context.drawCenteredTextWithShadow(SpeedrunOptionListWidget.this.client.textRenderer, this.category, x + entryWidth / 2, y + entryHeight / 2, 0xFFFFFF);
        }

        @Override
        public List<? extends Element> children() {
            return ImmutableList.of();
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return ImmutableList.of();
        }
    }
}


