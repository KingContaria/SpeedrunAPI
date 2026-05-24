package me.contaria.speedrunapi.config.screen.widgets.option;

import me.contaria.speedrunapi.config.option.NumberOption;
import me.contaria.speedrunapi.config.screen.widgets.IconButtonWidget;
import me.contaria.speedrunapi.util.IdentifierUtil;
import me.contaria.speedrunapi.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

@ApiStatus.Internal
public class NumberOptionTextFieldWidget<T extends NumberOption<?>> extends AbstractContainerWidget {
    private static final Identifier APPLY_SPRITE = IdentifierUtil.ofVanilla("container/beacon/confirm");

    private final NumberOption<?> option;
    private final EditBox editBox;
    private final Button applyButton;

    public NumberOptionTextFieldWidget(T option, int x, int y) {
        super(x, y, 150, 20, TextUtil.empty(), null);
        this.editBox = new EditBox(Minecraft.getInstance().font, x, y, 125, 20, TextUtil.empty());
        this.option = option;
        this.applyButton = new IconButtonWidget(APPLY_SPRITE, 0, 0, 18, 18, x + 130, y, TextUtil.empty(), button -> this.apply(), true);
        this.updateText();
        this.editBox.setResponder(string -> this.applyButton.active = !this.option.get().toString().equals(string));
    }

    private void apply() {
        try {
            this.option.setFromString(this.editBox.getValue());
            this.updateText();
        } catch (NumberFormatException e) {
            this.updateText();
        }
    }

    private void updateText() {
        this.editBox.setValue(this.option.get().toString());
        this.applyButton.active = false;
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        this.editBox.setX(this.getX());
        this.editBox.setY(this.getY());
        this.editBox.extractWidgetRenderState(graphics, mouseX, mouseY, a);
        this.applyButton.setX(this.getX() + 130);
        this.applyButton.setY(this.getY());
        this.applyButton.extractRenderState(graphics, mouseX, mouseY, a);
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return List.of(this.editBox, this.applyButton);
    }

    @Override
    protected int contentHeight() {
        return 0;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
    }
}
