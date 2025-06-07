package me.contaria.speedrunapi.config.toast;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
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
    public Visibility draw(MatrixStack matrices, ToastManager manager, long startTime) {
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
        RenderSystem.setShaderTexture(0, Toast.TEXTURE);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        if (description.size() < 2) {
            DrawableHelper.drawTexture(matrices, 0, 0, 0, 0, this.getWidth(), this.getHeight());
        } else {
            DrawableHelper.drawTexture(matrices, 0, 0, 0, 0, this.getWidth(), 11);
            int y = 8;
            for (int i = 0; i < description.size(); i++) {
                DrawableHelper.drawTexture(matrices, 0, y, 0, 11, this.getWidth(), 10);
                y += 10;
            }
            DrawableHelper.drawTexture(matrices, 0, y, 0, 21, this.getWidth(), 11);
        }

        manager.getClient().textRenderer.draw(matrices, this.title, 7.0f, 7.0f, 0xFFFF00 | 0xFF000000);

        float y = 18.0f;
        for (OrderedText line : description) {
            manager.getClient().textRenderer.draw(matrices, line, 7.0f, y, -1);
            y += 10.0f;
        }

        return this.fadeOut ? Visibility.HIDE : Visibility.SHOW;
    }

    @Override
    public int getHeight() {
        return Toast.super.getHeight() + Math.max(1, MinecraftClient.getInstance().textRenderer.wrapLines(this.description, this.getWidth() - 7).size() - 1) * 10;
    }
}
