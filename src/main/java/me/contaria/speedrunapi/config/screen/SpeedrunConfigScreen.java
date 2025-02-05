package me.contaria.speedrunapi.config.screen;

import me.contaria.speedrunapi.SpeedrunAPI;
import me.contaria.speedrunapi.config.SpeedrunConfigContainer;
import me.contaria.speedrunapi.config.screen.widgets.list.SpeedrunOptionListWidget;
import me.contaria.speedrunapi.util.TextUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.util.function.Predicate;

@ApiStatus.Internal
public class SpeedrunConfigScreen extends Screen {
    private final SpeedrunConfigContainer<?> config;
    @Nullable
    private final Predicate<InputUtil.Key> inputListener;
    private final Screen parent;

    private SpeedrunOptionListWidget list;
    private TextFieldWidget searchField;
    private boolean searchFieldOpen;

    public SpeedrunConfigScreen(SpeedrunConfigContainer<?> config, @Nullable Predicate<InputUtil.Key> inputListener, Screen parent) {
        super(TextUtil.literal(config.getModContainer().getMetadata().getName()));
        this.config = config;
        this.inputListener = inputListener;
        this.parent = parent;
    }

    private void toggleSearchField() {
        this.searchFieldOpen = !this.searchFieldOpen;
        this.searchField.setVisible(this.searchFieldOpen);
        if (this.searchFieldOpen) {
            this.setFocused(this.searchField);
            this.searchField.setFocusUnlocked(true);
            this.list.adjustTop(50);
        } else {
            this.searchField.setText("");
            this.list.adjustTop(25);
        }
    }

    @Override
    protected void init() {
        assert this.client != null;
        this.searchField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 25, 200, 20, this.searchField, TextUtil.translatable("speedrunapi.gui.config.search"));
        this.searchField.setVisible(this.searchFieldOpen);
        this.searchField.setChangedListener(string -> this.list.updateEntries(string));
        this.list = new SpeedrunOptionListWidget(this, this.config, this.client, this.width, this.height, 25, this.height - 32, this.searchField.getText());
        if (this.searchFieldOpen) {
            this.list.adjustTop(50);
        }
        this.addDrawableChild(this.list);
        this.addDrawableChild(this.searchField);
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close()).dimensions(this.width / 2 - 100, this.height - 27, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 10, 0xFFFFFF);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.inputListener != null && this.inputListener.test(InputUtil.fromKeyCode(keyCode, scanCode))) {
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_F && Screen.hasControlDown()) {
            this.toggleSearchField();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.inputListener != null && this.inputListener.test(InputUtil.Type.MOUSE.createFromCode(button))) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void close() {
        assert this.client != null;
        this.client.setScreen(this.parent);
    }

    @Override
    public void removed() {
        try {
            this.config.save();
        } catch (IOException e) {
            SpeedrunAPI.LOGGER.warn("Failed to save config file for {}.", this.config.getConfig().modID());
        }
    }
}
