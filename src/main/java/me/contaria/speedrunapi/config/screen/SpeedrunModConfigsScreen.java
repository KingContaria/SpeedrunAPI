package me.contaria.speedrunapi.config.screen;

import me.contaria.speedrunapi.config.SpeedrunConfigAPI;
import me.contaria.speedrunapi.config.api.gui.CallbackButtonWidget;
import me.contaria.speedrunapi.config.screen.widgets.list.SpeedrunModConfigListWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class SpeedrunModConfigsScreen extends AbstractSpeedrunConfigAPIScreen {
    private final Screen parent;
    private final String title;
    private SpeedrunModConfigListWidget list;

    public SpeedrunModConfigsScreen(Screen parent) {
        this.parent = parent;
        this.title = I18n.translate("speedrunapi.gui.config.title");
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        this.list.render(mouseX, mouseY, delta);
        this.drawCenteredString(this.client.textRenderer, this.title, this.width / 2, 10, 0xFFFFFF);
        super.render(mouseX, mouseY, delta);
    }

    @Override
    public void init() {
        this.list = new SpeedrunModConfigListWidget(SpeedrunConfigAPI.getModConfigScreenProviders(), this, this.client, this.width, this.height, 25, this.height - 32);
        this.buttons.add(new CallbackButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20, I18n.translate("gui.done"), button -> this.onClose()));
    }

    @Override
    protected void keyPressed(char id, int code) {
        if (code == 1) {
            this.onClose();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        this.list.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void handleMouse() {
        super.handleMouse();
        this.list.handleMouse();
    }

    public void onClose() {
        this.client.setScreen(this.parent);
    }
}
