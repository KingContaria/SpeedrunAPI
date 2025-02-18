package me.contaria.speedrunapi.config.screen.widgets;

import me.contaria.speedrunapi.util.TextUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class IconButtonWidget extends ButtonWidget {
    private final Identifier texture;
    private final int u;
    private final int v;
    private final int textureWidth;
    private final int textureHeight;
    private final Text title;

    public IconButtonWidget(Identifier texture, int x, int y, PressAction onPress) {
        this(texture, 0, 0, 16, 16, x, y, TextUtil.empty(), onPress);
    }

    public IconButtonWidget(Identifier texture, int x, int y, Text title, PressAction onPress) {
        this(texture, 0, 0, 16, 16, x, y, title, onPress);
    }

    public IconButtonWidget(Identifier texture, int u, int v, int textureWidth, int textureHeight, int x, int y, PressAction onPress) {
        this(texture, u, v, textureWidth, textureHeight, x, y, TextUtil.empty(), onPress);
    }

    public IconButtonWidget(Identifier texture, int u, int v, int textureWidth, int textureHeight, int x, int y, Text title, PressAction onPress) {
        super(x, y, 20, 20, TextUtil.empty(), onPress);
        this.texture = texture;
        this.u = u;
        this.v = v;
        this.title = title;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.renderButton(matrices, mouseX, mouseY, delta);
        MinecraftClient.getInstance().getTextureManager().bindTexture(this.texture);
        drawTexture(matrices, this.x + 2, this.y + 2, this.u, this.v, 16, 16, this.textureWidth, this.textureHeight);
    }

    @Override
    public void renderToolTip(MatrixStack matrices, int mouseX, int mouseY) {
        this.drawCenteredText(matrices, MinecraftClient.getInstance().textRenderer, this.title, this.x + this.getWidth() / 2, this.y - 15, 16777215);
    }
}
