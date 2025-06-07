package me.contaria.speedrunapi.config.toast;

import me.contaria.speedrunapi.util.IdentifierUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class SpeedrunConfigErrorToast implements Toast {
    private static final Identifier TEXTURE = IdentifierUtil.ofVanilla("toast/advancement");

    private final Text title;
    private final Text description;
    private Screen firstScreen;
    private boolean fadeOut;

    public SpeedrunConfigErrorToast(Text title, Text description) {
        this.title = title;
        this.description = description;
    }

    @Override
    public Visibility getVisibility() {
        return this.fadeOut ? Visibility.HIDE : Visibility.SHOW;
    }

    @Override
    public void update(ToastManager manager, long time) {
    }

    @Override
    public void draw(DrawContext context, TextRenderer textRenderer, long startTime) {
        if (MinecraftClient.getInstance().getOverlay() != null) {
            return;
        }

        if (this.firstScreen == null) {
            this.firstScreen = MinecraftClient.getInstance().currentScreen;
        }
        if (this.firstScreen != MinecraftClient.getInstance().currentScreen) {
            this.fadeOut = true;
        }

        List<OrderedText> description = textRenderer.wrapLines(this.description, this.getWidth() - 7);
        if (description.size() < 2) {
            context.drawGuiTexture(RenderLayer::getGuiTextured, TEXTURE, 0, 0, this.getWidth(), this.getHeight());
        } else {
            context.drawGuiTexture(RenderLayer::getGuiTextured, TEXTURE, 160, 32, 0, 0, 0, 0, this.getWidth(), 11);
            int y = 8;
            for (int i = 0; i < description.size(); i++) {
                context.drawGuiTexture(RenderLayer::getGuiTextured, TEXTURE, 160, 32, 0, 11, 0, y, this.getWidth(), 10);
                y += 10;
            }
            context.drawGuiTexture(RenderLayer::getGuiTextured, TEXTURE, 160, 32, 0, 21, 0, y, this.getWidth(), 11);
        }

        context.drawText(textRenderer, this.title, 7, 7, 0xFFFF00 | 0xFF000000, true);

        int y = 18;
        for (OrderedText line : description) {
            context.drawText(textRenderer, line, 7, y, -1, true);
            y += 10;
        }
    }

    @Override
    public int getHeight() {
        return Toast.super.getHeight() + Math.max(1, MinecraftClient.getInstance().textRenderer.wrapLines(this.description, this.getWidth() - 7).size() - 1) * 10;
    }
}
