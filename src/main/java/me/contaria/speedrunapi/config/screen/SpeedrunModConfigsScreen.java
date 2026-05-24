package me.contaria.speedrunapi.config.screen;

import me.contaria.speedrunapi.config.SpeedrunConfigAPI;
import me.contaria.speedrunapi.config.screen.widgets.list.SpeedrunModConfigListWidget;
import me.contaria.speedrunapi.util.TextUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.CommonComponents;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class SpeedrunModConfigsScreen extends Screen {
    private final Screen parent;

    public SpeedrunModConfigsScreen(Screen parent) {
        super(TextUtil.translatable("speedrunapi.gui.config.title"));
        this.parent = parent;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.centeredText(this.font, this.title, this.width / 2, 10, 0xffffffff);
    }

    @Override
    public void onClose() {
        assert this.minecraft != null;
        this.minecraft.setScreen(this.parent);
    }

    @Override
    protected void init() {
        this.addRenderableWidget(new SpeedrunModConfigListWidget(SpeedrunConfigAPI.getModConfigScreenProviders(), this, this.minecraft, this.width, this.height - 57, 25));
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> this.onClose()).bounds(this.width / 2 - 100, this.height - 27, 200, 20).build());
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        return super.keyPressed(event);
    }
}
