package me.contaria.speedrunapi.config.screen.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class IconButtonWidget extends ButtonWidget {
    private final Identifier texture;
    private final int u;
    private final int v;
    private final int textureWidth;
    private final int textureHeight;
    private final String title;

    public IconButtonWidget(Identifier texture, int x, int y, PressAction onPress) {
        this(texture, 0, 0, 16, 16, x, y, "", onPress);
    }

    public IconButtonWidget(Identifier texture, int x, int y, String title, PressAction onPress) {
        this(texture, 0, 0, 16, 16, x, y, title, onPress);
    }

    public IconButtonWidget(Identifier texture, int u, int v, int textureWidth, int textureHeight, int x, int y, PressAction onPress) {
        this(texture, u, v, textureWidth, textureHeight, x, y, "", onPress);
    }

    public IconButtonWidget(Identifier texture, int u, int v, int textureWidth, int textureHeight, int x, int y, String title, PressAction onPress) {
        super(x, y, 20, 20, "", onPress);
        this.texture = texture;
        this.u = u;
        this.v = v;
        this.title = title;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float delta) {
        super.renderButton(mouseX, mouseY, delta);
        MinecraftClient.getInstance().getTextureManager().bindTexture(this.texture);
        DrawableHelper.blit(this.x + 2, this.y + 2, this.u, this.v, 16, 16, this.textureWidth, this.textureHeight);
    }

    @Override
    public void renderToolTip(int mouseX, int mouseY) {
        this.drawCenteredString(MinecraftClient.getInstance().textRenderer, this.title, this.x + this.getWidth() / 2, this.y - 15, 16777215);
    }
}
