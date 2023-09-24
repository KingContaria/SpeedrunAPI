package org.mcsr.speedrunapi.config.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import org.mcsr.speedrunapi.config.SpeedrunConfigAPI;
import org.mcsr.speedrunapi.config.screen.widgets.list.SpeedrunModConfigListWidget;

public class SpeedrunModConfigsScreen extends Screen {

    private final Screen parent;
    private SpeedrunModConfigListWidget list;

    public SpeedrunModConfigsScreen(Screen parent) {
        super(new TranslatableText("speedrunapi.gui.speedrunConfigTitle"));
        this.parent = parent;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.list.render(matrices, mouseX, mouseY, delta);
        this.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 10, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        assert this.client != null;
        this.client.openScreen(this.parent);
    }

    @Override
    protected void init() {
        this.list = new SpeedrunModConfigListWidget(SpeedrunConfigAPI.getModConfigScreenProviders(), this, this.client, this.width, this.height, 25, this.height - 32);
        this.addChild(this.list);
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20, ScreenTexts.DONE, button -> this.onClose()));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return this.list.keyPressed(keyCode, scanCode, modifiers);
    }
}
