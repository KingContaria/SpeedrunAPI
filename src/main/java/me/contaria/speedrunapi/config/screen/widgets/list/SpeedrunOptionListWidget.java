package me.contaria.speedrunapi.config.screen.widgets.list;

import me.contaria.speedrunapi.config.SpeedrunConfigAPI;
import me.contaria.speedrunapi.config.SpeedrunConfigContainer;
import me.contaria.speedrunapi.config.api.SpeedrunOption;
import me.contaria.speedrunapi.config.api.gui.ButtonWidgetCallback;
import me.contaria.speedrunapi.config.api.gui.SpeedrunWidget;
import me.contaria.speedrunapi.config.screen.SpeedrunConfigScreen;
import me.contaria.speedrunapi.config.screen.widgets.TextWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.resource.language.I18n;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@ApiStatus.Internal
public class SpeedrunOptionListWidget extends EntryListWidget {
    private final SpeedrunConfigScreen parent;
    private final SpeedrunConfigContainer<?> config;
    private final List<OptionListEntry> entries;

    @Nullable
    private TextWidget tooltipToRender;

    public SpeedrunOptionListWidget(SpeedrunConfigScreen parent, SpeedrunConfigContainer<?> config, MinecraftClient client, int width, int height, int top, int bottom, String filter) {
        super(client, width, height, top, bottom, 30);
        this.parent = parent;
        this.config = config;
        this.entries = new ArrayList<>();
        this.updateEntries(filter);
    }

    public void keyPressed(char id, int code) {
        for (int i = 0; i < this.entries.size(); i++) {
            this.entries.get(i).keyPressed(i, id, code);
        }
    }

    public void mouseDragged(int mouseX, int mouseY, int button, long mouseLastClicked) {
        for (int i = 0; i < this.entries.size(); i++) {
            int x = mouseX - (this.xStart + this.width / 2 - this.getRowWidth() / 2 + 2);
            int y = mouseY - (this.yStart + 4 - this.getScrollAmount() + i * this.entryHeight + this.headerHeight);
            this.entries.get(i).mouseDragged(i, mouseX, mouseY, button, x, y, mouseLastClicked);
        }
    }

    public void tick() {
        for (OptionListEntry entry : this.entries) {
            entry.tick();
        }
    }

    @Override
    public Entry getEntry(int index) {
        return this.entries.get(index);
    }

    @Override
    protected int getEntryCount() {
        return this.entries.size();
    }

    public void updateEntries(String filter) {
        this.entries.clear();

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
            this.entries.add(new OptionEntry(option));
        }

        for (Map.Entry<String, Set<SpeedrunOption<?>>> category : categorizedOptions.entrySet()) {
            if (!this.config.getConfig().shouldShowCategory(category.getKey())) {
                continue;
            }
            String categoryTranslation = "speedrunapi.config." + this.config.getModContainer().getMetadata().getId() + ".category." + category.getKey();
            if (!SpeedrunConfigAPI.hasTranslation(categoryTranslation) && SpeedrunConfigAPI.hasTranslation(category.getKey())) {
                categoryTranslation = category.getKey();
            }
            this.entries.add(new OptionCategoryEntry(I18n.translate(categoryTranslation)));
            for (SpeedrunOption<?> option : category.getValue()) {
                this.entries.add(new OptionEntry(option));
            }
        }

        this.scrollAmount = 0.0f;
    }

    public void adjustTop(int top) {
        this.yStart = top;
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

    public abstract class OptionListEntry implements EntryListWidget.Entry {
        public void keyPressed(int index, char id, int code) {
        }

        @Override
        public boolean mouseClicked(int index, int mouseX, int mouseY, int button, int x, int y) {
            return false;
        }

        @Override
        public void mouseReleased(int index, int mouseX, int mouseY, int button, int x, int y) {
        }

        public void mouseDragged(int index, int mouseX, int mouseY, int button, int x, int y, long mouseLastClicked) {
        }

        public void tick() {
        }

        @Override
        public void updatePosition(int index, int x, int y) {
        }
    }

    public class OptionEntry extends OptionListEntry {
        private final TextWidget text;
        private final Object widget;

        public OptionEntry(SpeedrunOption<?> option) {
            this.text = new TextWidget(SpeedrunOptionListWidget.this.parent, SpeedrunOptionListWidget.this.client.textRenderer, option.getName(), option.getDescription(), SpeedrunOptionListWidget.this.yStart, SpeedrunOptionListWidget.this.yEnd);
            this.widget = option.createWidget();
            if (!(this.widget instanceof ButtonWidget || this.widget instanceof SpeedrunWidget)) {
                throw new RuntimeException("Return value of SpeedrunOption#createWidget is not a ButtonWidget or SpeedrunWidget!");
            }
        }

        @Override
        public void render(int index, int x, int y, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered) {
            this.text.x = x + 5;
            int yOffset = (20 - SpeedrunOptionListWidget.this.client.textRenderer.fontHeight) / 2;
            this.text.y = y + 5 + yOffset;
            this.text.renderText();

            if (this.widget instanceof ButtonWidget) {
                ButtonWidget button = (ButtonWidget) this.widget;
                button.x = x + rowWidth - button.getWidth() - 5;
                button.y = y + 5;
                button.render(SpeedrunOptionListWidget.this.client, mouseX, mouseY);
            } else {
                SpeedrunWidget widget = (SpeedrunWidget) this.widget;
                widget.setX(x + rowWidth - widget.getWidth() - 5);
                widget.setY(y + 5);
                widget.render(mouseX, mouseY);
            }

            if (hovered && this.text.isMouseOver(mouseX, mouseY)) {
                SpeedrunOptionListWidget.this.tooltipToRender = this.text;
            }
        }

        @Override
        public void keyPressed(int index, char id, int code) {
            if (this.widget instanceof SpeedrunWidget) {
                ((SpeedrunWidget) this.widget).keyPressed(id, code);
            }
        }

        @Override
        public boolean mouseClicked(int index, int mouseX, int mouseY, int button, int x, int y) {
            if (this.widget instanceof SpeedrunWidget) {
                return ((SpeedrunWidget) this.widget).mouseClicked(mouseX, mouseY, button);
            } else {
                ButtonWidget buttonWidget = ((ButtonWidget) this.widget);
                if (buttonWidget.isMouseOver(MinecraftClient.getInstance(), mouseX, mouseY)) {
                    if (buttonWidget instanceof ButtonWidgetCallback) {
                        buttonWidget.playDownSound(MinecraftClient.getInstance().getSoundManager());
                        ((ButtonWidgetCallback) buttonWidget).onPress();
                    }
                    return true;
                }
            }
            return false;
        }

        @Override
        public void mouseReleased(int index, int mouseX, int mouseY, int button, int x, int y) {
            if (this.widget instanceof SpeedrunWidget) {
                ((SpeedrunWidget) this.widget).mouseReleased(mouseX, mouseY, button);
            } else {
                ((ButtonWidget) this.widget).mouseReleased(mouseX, mouseY);
            }
        }

        @Override
        public void mouseDragged(int index, int mouseX, int mouseY, int button, int x, int y, long mouseLastClicked) {
            if (this.widget instanceof SpeedrunWidget) {
                ((SpeedrunWidget) this.widget).mouseDragged(mouseX, mouseY, button, mouseLastClicked);
            }
        }

        @Override
        public void tick() {
            if (this.widget instanceof SpeedrunWidget) {
                ((SpeedrunWidget) this.widget).tick();
            }
        }
    }

    public class OptionCategoryEntry extends OptionListEntry {
        private final String category;

        public OptionCategoryEntry(String category) {
            this.category = category;
        }

        @Override
        public void render(int index, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered) {
            SpeedrunOptionListWidget.this.parent.drawCenteredString(SpeedrunOptionListWidget.this.client.textRenderer, this.category, x + entryWidth / 2, y + entryHeight / 2, 0xFFFFFF);
        }
    }
}


