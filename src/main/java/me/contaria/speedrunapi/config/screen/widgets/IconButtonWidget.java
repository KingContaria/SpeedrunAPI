package me.contaria.speedrunapi.config.screen.widgets;

import me.contaria.speedrunapi.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

@ApiStatus.Internal
public class IconButtonWidget extends Button.Plain {
    private final Identifier texture;
    private final int u;
    private final int v;
    private final int textureWidth;
    private final int textureHeight;
    private final Component title;
    private final boolean sprite;

    public IconButtonWidget(Identifier texture, int x, int y, OnPress onPress) {
        this(texture, 0, 0, 16, 16, x, y, TextUtil.empty(), onPress, false);
    }

    public IconButtonWidget(Identifier texture, int x, int y, Component title, OnPress onPress) {
        this(texture, 0, 0, 16, 16, x, y, title, onPress, false);
    }

    public IconButtonWidget(Identifier texture, int u, int v, int textureWidth, int textureHeight, int x, int y, OnPress onPress) {
        this(texture, u, v, textureWidth, textureHeight, x, y, TextUtil.empty(), onPress, false);
    }

    public IconButtonWidget(Identifier texture, int u, int v, int textureWidth, int textureHeight, int x, int y, Component title, OnPress onPress, boolean sprite) {
        super(x, y, 20, 20, TextUtil.empty(), onPress, Supplier::get);
        this.texture = texture;
        this.u = u;
        this.v = v;
        this.title = title;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.sprite = sprite;
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractContents(graphics, mouseX, mouseY, a);
        if (!this.sprite) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, this.texture, this.getX() + 2, this.getY() + 2, this.u, this.v, 16, 16, this.textureWidth, this.textureHeight);
        } else {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.texture, this.textureWidth, this.textureHeight, 0, 0, this.getX() + 2, this.getY() + 2, 16, 16);
        }
        if (this.isMouseOver(mouseX, mouseY)) {
            graphics.centeredText(Minecraft.getInstance().font, this.title, this.getX() + this.getWidth() / 2, this.getY() - 15, 0xffffffff);
        }
    }
}
