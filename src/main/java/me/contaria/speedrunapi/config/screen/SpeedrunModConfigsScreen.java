package me.contaria.speedrunapi.config.screen;

import me.contaria.speedrunapi.config.SpeedrunConfigAPI;
import me.contaria.speedrunapi.config.screen.widgets.list.SpeedrunModConfigListWidget;
import me.contaria.speedrunapi.util.TextUtil;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class SpeedrunModConfigsScreen extends Screen {
    private final Screen parent;

    public SpeedrunModConfigsScreen(Screen parent) {
        super(TextUtil.translatable("speedrunapi.gui.config.title"));
        this.parent = parent;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        DrawableHelper.drawCenteredTextWithShadow(matrices, this.textRenderer, this.title, this.width / 2, 10, 0xFFFFFF);
    }

    @Override
    public void close() {
        assert this.client != null;
        this.client.setScreen(this.parent);
    }

    @Override
    protected void init() {
        this.addDrawableChild(new SpeedrunModConfigListWidget(SpeedrunConfigAPI.getModConfigScreenProviders(), this, this.client, this.width, this.height, 25, this.height - 32));
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close()).dimensions(this.width / 2 - 100, this.height - 27, 200, 20).build());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
