package me.contaria.speedrunapi.config.screen.widgets;

import me.contaria.speedrunapi.config.api.gui.CallbackButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

@ApiStatus.Internal
public class IconButtonWidget extends CallbackButtonWidget {
    private final Identifier texture;
    private final int u;
    private final int v;
    private final int textureWidth;
    private final int textureHeight;
    private final String title;

    public IconButtonWidget(int id, Identifier texture, int x, int y, Consumer<CallbackButtonWidget> onPress) {
        this(id, texture, 0, 0, 16, 16, x, y, "", onPress);
    }

    public IconButtonWidget(int id, Identifier texture, int x, int y, String title, Consumer<CallbackButtonWidget> onPress) {
        this(id, texture, 0, 0, 16, 16, x, y, title, onPress);
    }

    public IconButtonWidget(int id, Identifier texture, int u, int v, int textureWidth, int textureHeight, int x, int y, Consumer<CallbackButtonWidget> onPress) {
        this(id, texture, u, v, textureWidth, textureHeight, x, y, "", onPress);
    }

    public IconButtonWidget(int id, Identifier texture, int u, int v, int textureWidth, int textureHeight, int x, int y, String title, Consumer<CallbackButtonWidget> onPress) {
        super(id, x, y, 20, 20, "", onPress);
        this.texture = texture;
        this.u = u;
        this.v = v;
        this.title = title;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    @Override
    public void render(MinecraftClient client, int mouseX, int mouseY) {
        super.render(client, mouseX, mouseY);
        MinecraftClient.getInstance().getTextureManager().bindTexture(this.texture);
        DrawableHelper.drawTexture(this.x + 2, this.y + 2, this.u, this.v, 16, 16, this.textureWidth, this.textureHeight);
    }

    @Override
    public void renderToolTip(int mouseX, int mouseY) {
        this.drawCenteredString(MinecraftClient.getInstance().textRenderer, this.title, this.x + this.getWidth() / 2, this.y - 15, 16777215);
    }
}
