package me.contaria.speedrunapi.config.screen;

import me.contaria.speedrunapi.config.SpeedrunConfigAPI;
import me.contaria.speedrunapi.config.screen.widgets.list.SpeedrunModConfigListWidget;
import me.contaria.speedrunapi.util.TextUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class SpeedrunModConfigsScreen extends Screen {
    private final Screen parent;
    private SpeedrunModConfigListWidget list;

    public SpeedrunModConfigsScreen(Screen parent) {
        super(TextUtil.translatable("speedrunapi.gui.config.title"));
        this.parent = parent;
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        assert this.minecraft != null;
        this.renderBackground();
        this.list.render(mouseX, mouseY, delta);
        this.drawCenteredString(this.minecraft.textRenderer, this.title.asFormattedString(), this.width / 2, 10, 0xFFFFFF);
        super.render(mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        assert this.minecraft != null;
        this.minecraft.openScreen(this.parent);
    }

    @Override
    protected void init() {
        this.list = new SpeedrunModConfigListWidget(SpeedrunConfigAPI.getModConfigScreenProviders(), this, this.minecraft, this.width, this.height, 25, this.height - 32);
        this.children.add(this.list);
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20, I18n.translate("gui.done"), button -> this.onClose()));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers) || this.list.keyPressed(keyCode, scanCode, modifiers);
    }
}
