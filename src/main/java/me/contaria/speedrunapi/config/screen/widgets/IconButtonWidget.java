package me.contaria.speedrunapi.config.screen.widgets;

import me.contaria.speedrunapi.util.TextUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

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
        super(x, y, 20, 20, TextUtil.empty(), onPress, Supplier::get);
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
        RenderSystem.setShaderTexture(0, this.texture);
        DrawableHelper.drawTexture(matrices, this.getX() + 2, this.getY() + 2, this.u, this.v, 16, 16, this.textureWidth, this.textureHeight);
        if (this.isMouseOver(mouseX, mouseY)) {
            DrawableHelper.drawCenteredTextWithShadow(matrices, MinecraftClient.getInstance().textRenderer, this.title, this.getX() + this.getWidth() / 2, this.getY() - 15, 16777215);
        }
    }
}
