package me.contaria.speedrunapi.config.toast;

import me.contaria.speedrunapi.util.IdentifierUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class SpeedrunConfigErrorToast implements Toast {
    private static final Identifier TEXTURE = IdentifierUtil.ofVanilla("toast/advancement");

    private final Component title;
    private final Component description;
    private Screen firstScreen;
    private boolean fadeOut;

    public SpeedrunConfigErrorToast(Component title, Component description) {
        this.title = title;
        this.description = description;
    }

    @Override
    public Visibility getWantedVisibility() {
        return this.fadeOut ? Visibility.HIDE : Visibility.SHOW;
    }

    @Override
    public void update(ToastManager manager, long time) {
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, Font font, long fullyVisibleForMs) {
        if (Minecraft.getInstance().gui.overlay() != null) {
            return;
        }

        if (this.firstScreen == null) {
            this.firstScreen = Minecraft.getInstance().gui.screen();
        }
        if (this.firstScreen != Minecraft.getInstance().gui.screen()) {
            this.fadeOut = true;
        }

        List<FormattedCharSequence> description = font.split(this.description, this.width() - 7);
        if (description.size() < 2) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TEXTURE, 0, 0, this.width(), this.height());
        } else {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TEXTURE, 160, 32, 0, 0, 0, 0, this.width(), 11);
            int y = 8;
            for (int i = 0; i < description.size(); i++) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TEXTURE, 160, 32, 0, 11, 0, y, this.width(), 10);
                y += 10;
            }
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TEXTURE, 160, 32, 0, 21, 0, y, this.width(), 11);
        }

        graphics.text(font, this.title, 7, 7, 0xFFFF00 | 0xFF000000, true);

        int y = 18;
        for (FormattedCharSequence line : description) {
            graphics.text(font, line, 7, y, -1, true);
            y += 10;
        }
    }

    @Override
    public int height() {
        return Toast.super.height() + Math.max(1, Minecraft.getInstance().font.split(this.description, this.width() - 7).size() - 1) * 10;
    }
}
