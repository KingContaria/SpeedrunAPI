package me.contaria.speedrunapi.config.screen;

import me.contaria.speedrunapi.SpeedrunAPI;
import me.contaria.speedrunapi.config.SpeedrunConfigContainer;
import me.contaria.speedrunapi.config.api.gui.ButtonWidgetCallback;
import me.contaria.speedrunapi.config.api.gui.CallbackButtonWidget;
import me.contaria.speedrunapi.config.screen.widgets.list.SpeedrunOptionListWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PagedEntryListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.function.Predicate;

@ApiStatus.Internal
public class SpeedrunConfigScreen extends AbstractSpeedrunConfigAPIScreen {
    private final SpeedrunConfigContainer<?> config;
    @Nullable
    private final Predicate<Integer> inputListener;
    private final Screen parent;
    private final String title;

    private SpeedrunOptionListWidget list;
    private TextFieldWidget searchField;
    private boolean searchFieldOpen;

    public SpeedrunConfigScreen(SpeedrunConfigContainer<?> config, @Nullable Predicate<Integer> inputListener, Screen parent) {
        this.config = config;
        this.inputListener = inputListener;
        this.parent = parent;
        this.title = config.getModContainer().getMetadata().getName();
    }

    private void toggleSearchField() {
        this.searchFieldOpen = !this.searchFieldOpen;
        this.searchField.setVisible(this.searchFieldOpen);
        if (this.searchFieldOpen) {
            this.searchField.setFocused(true);
            this.list.adjustTop(50);
        } else {
            this.searchField.setText("");
            this.list.adjustTop(25);
        }
    }

    @Override
    public void init() {
        String search = this.searchField != null ? this.searchField.getText() : "";
        this.searchField = new TextFieldWidget(-1, this.client.textRenderer, this.width / 2 - 100, 25, 200, 20);
        this.searchField.setText(search);
        this.searchField.setVisible(this.searchFieldOpen);
        this.searchField.setListener(new PagedEntryListWidget.Listener() {
            @Override
            public void setBooleanValue(int id, boolean value) {
            }

            @Override
            public void setFloatValue(int id, float value) {
            }

            @Override
            public void setStringValue(int id, String text) {
                SpeedrunConfigScreen.this.list.updateEntries(text);
            }
        });
        this.list = new SpeedrunOptionListWidget(this, this.config, this.client, this.width, this.height, 25, this.height - 32, this.searchField.getText());
        if (this.searchFieldOpen) {
            this.list.adjustTop(50);
        }
        this.buttons.add(new CallbackButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20, I18n.translate("gui.done"), button -> this.onClose()));
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        this.list.render(mouseX, mouseY, delta);
        this.searchField.render();
        this.drawCenteredString(this.client.textRenderer, this.title, this.width / 2, 10, 0xFFFFFF);
        super.render(mouseX, mouseY, delta);
    }

    @Override
    protected void keyPressed(char id, int code) {
        if (this.inputListener != null && this.inputListener.test(code != 0 ? code : id + 256)) {
            return;
        }
        if (code == 33 && Screen.hasControlDown()) {
            this.toggleSearchField();
            return;
        }
        if (this.searchField.keyPressed(id, code)) {
            return;
        }
        if (code == 1) {
            this.onClose();
            return;
        }
        this.list.keyPressed(id, code);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        if (this.inputListener != null && this.inputListener.test(button - 100)) {
            return;
        }
        super.mouseClicked(mouseX, mouseY, button);
        this.searchField.mouseClicked(mouseX, mouseY, button);
        this.list.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);
        this.list.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    protected void mouseDragged(int mouseX, int mouseY, int button, long mouseLastClicked) {
        super.mouseDragged(mouseX, mouseY, button, mouseLastClicked);
        this.list.mouseDragged(mouseX, mouseY, button, mouseLastClicked);
    }

    @Override
    public void handleMouse() {
        super.handleMouse();
        this.list.handleMouse();
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if (button instanceof ButtonWidgetCallback) {
            ((ButtonWidgetCallback) button).onPress();
        }
    }

    @Override
    public void tick() {
        this.list.tick();
        this.searchField.tick();
    }

    public void onClose() {
        this.client.setScreen(this.parent);
        this.config.getConfig().onConfigScreenClose(this, this.parent);
    }

    @Override
    public void removed() {
        Keyboard.enableRepeatEvents(false);
        try {
            this.config.save();
        } catch (IOException e) {
            SpeedrunAPI.LOGGER.warn("Failed to save config file for {}.", this.config.getConfig().modID());
        }
    }
}
