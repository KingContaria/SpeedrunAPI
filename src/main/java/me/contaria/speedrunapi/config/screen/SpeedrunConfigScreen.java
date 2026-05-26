package me.contaria.speedrunapi.config.screen;

import com.mojang.blaze3d.platform.InputConstants;
import me.contaria.speedrunapi.SpeedrunAPI;
import me.contaria.speedrunapi.config.SpeedrunConfigContainer;
import me.contaria.speedrunapi.config.screen.widgets.list.SpeedrunOptionListWidget;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.util.function.Predicate;

@ApiStatus.Internal
public class SpeedrunConfigScreen extends Screen {
    private final SpeedrunConfigContainer<?> config;
    @Nullable
    private final Predicate<InputConstants.Key> inputListener;
    private final Screen parent;

    private SpeedrunOptionListWidget list;
    private EditBox searchField;
    private boolean searchFieldOpen;

    public SpeedrunConfigScreen(SpeedrunConfigContainer<?> config, @Nullable Predicate<InputConstants.Key> inputListener, Screen parent) {
        super(Component.literal(config.getModContainer().getMetadata().getName()));
        this.config = config;
        this.inputListener = inputListener;
        this.parent = parent;
    }

    private void toggleSearchField() {
        this.searchFieldOpen = !this.searchFieldOpen;
        this.searchField.setVisible(this.searchFieldOpen);
        if (this.searchFieldOpen) {
            this.setFocused(this.searchField);
            this.searchField.setCanLoseFocus(true);
            this.list.setY(50);
            this.list.setHeight(this.height - 57 - 25);
        } else {
            this.searchField.setValue("");
            this.list.setY(25);
            this.list.setHeight(this.height - 57);
        }
    }

    @Override
    protected void init() {
        assert this.minecraft != null;
        this.searchField = new EditBox(this.font, this.width / 2 - 100, 25, 200, 20, this.searchField, Component.translatable("speedrunapi.gui.config.search"));
        this.searchField.setVisible(this.searchFieldOpen);
        this.searchField.setResponder(string -> this.list.updateEntries(string));
        this.list = new SpeedrunOptionListWidget(this, this.config, this.minecraft, this.width, this.height - 57, 25, this.searchField.getValue());
        if (this.searchFieldOpen) {
            this.list.setY(50);
            this.list.setHeight(this.height - 57 - 25);
        }
        this.addRenderableWidget(this.list);
        this.addRenderableWidget(this.searchField);
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> this.onClose()).bounds(this.width / 2 - 100, this.height - 27, 200, 20).build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.centeredText(this.font, this.title, this.width / 2, 10, 0xFFFFFFff);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.inputListener != null && this.inputListener.test(InputConstants.getKey(event))) {
            return true;
        }
        if (event.key() == GLFW.GLFW_KEY_F && event.hasControlDown()) {
            this.toggleSearchField();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (this.inputListener != null && this.inputListener.test(InputConstants.Type.MOUSE.getOrCreate(event.button()))) {
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public void onClose() {
        assert this.minecraft != null;
        this.minecraft.setScreen(this.parent);
        this.config.getConfig().onConfigScreenClose(this, this.parent);
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
