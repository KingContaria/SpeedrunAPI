package me.contaria.speedrunapi.config.toast;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.List;

public class SpeedrunConfigErrorToast implements Toast {
    private final Text title;
    private final Text description;
    private Screen firstScreen;
    private boolean fadeOut;

    public SpeedrunConfigErrorToast(Text title, Text description) {
        this.title = title;
        this.description = description;
    }

    @Override
    public Visibility draw(DrawContext context, ToastManager manager, long startTime) {
        if (manager.getClient().getOverlay() != null) {
            return Visibility.SHOW;
        }

        if (this.firstScreen == null) {
            this.firstScreen = manager.getClient().currentScreen;
        }
        if (this.firstScreen != manager.getClient().currentScreen) {
            this.fadeOut = true;
        }

        List<OrderedText> description = manager.getClient().textRenderer.wrapLines(this.description, this.getWidth() - 7);
        if (description.size() < 2) {
            context.drawTexture(Toast.TEXTURE, 0, 0, 0, 0, this.getWidth(), this.getHeight());
        } else {
            context.drawTexture(Toast.TEXTURE, 0, 0, 0, 0, this.getWidth(), 11);
            int y = 8;
            for (int i = 0; i < description.size(); i++) {
                context.drawTexture(Toast.TEXTURE, 0, y, 0, 11, this.getWidth(), 10);
                y += 10;
            }
            context.drawTexture(Toast.TEXTURE, 0, y, 0, 21, this.getWidth(), 11);
        }

        context.drawText(manager.getClient().textRenderer, this.title, 7, 7, 0xFFFF00 | 0xFF000000, true);

        int y = 18;
        for (OrderedText line : description) {
            context.drawText(manager.getClient().textRenderer, line, 7, y, -1, true);
            y += 10;
        }

        return this.fadeOut ? Visibility.HIDE : Visibility.SHOW;
    }

    @Override
    public int getHeight() {
        return Toast.super.getHeight() + Math.max(1, MinecraftClient.getInstance().textRenderer.wrapLines(this.description, this.getWidth() - 7).size() - 1) * 10;
    }
}
