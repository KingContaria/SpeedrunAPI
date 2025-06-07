package me.contaria.speedrunapi.config.toast;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;

import java.util.List;

public class SpeedrunConfigErrorToast implements Toast {
    private final String titleKey;
    private final String descriptionKey;
    private final Object[] descriptionArgs;
    private Screen firstScreen;
    private boolean fadeOut;

    public SpeedrunConfigErrorToast(String titleKey, String descriptionKey, Object... descriptionArgs) {
        this.titleKey = titleKey;
        this.descriptionKey = descriptionKey;
        this.descriptionArgs = descriptionArgs;
    }

    @Override
    public Visibility draw(ToastManager manager, long startTime) {
        if (manager.getGame().getOverlay() != null) {
            return Visibility.SHOW;
        }

        if (this.firstScreen == null) {
            this.firstScreen = manager.getGame().currentScreen;
        }
        if (this.firstScreen != manager.getGame().currentScreen) {
            this.fadeOut = true;
        }

        List<String> description = manager.getGame().textRenderer.wrapStringToWidthAsList(I18n.translate(this.descriptionKey, this.descriptionArgs), 160 - 7);
        manager.getGame().getTextureManager().bindTexture(TOASTS_TEX);
        if (description.size() < 2) {
            manager.blit(0, 0, 0, 0, 160, 32);
        } else {
            manager.blit(0, 0, 0, 0, 160, 11);
            int y = 8;
            for (int i = 0; i < description.size(); i++) {
                manager.blit(0, y, 0, 11, 160, 10);
                y += 10;
            }
            manager.blit(0, y, 0, 21, 160, 11);
        }

        manager.getGame().textRenderer.draw(I18n.translate(this.titleKey), 7.0f, 7.0f, 0xFFFF00 | 0xFF000000);

        float y = 18.0f;
        for (String line : description) {
            manager.getGame().textRenderer.draw(line, 7.0f, y, -1);
            y += 10.0f;
        }

        return this.fadeOut ? Visibility.HIDE : Visibility.SHOW;
    }

//    @Override
//    public int getHeight() {
//        return Toast.super.getHeight() + Math.max(1, MinecraftClient.getInstance().textRenderer.wrapLines(this.description, this.getWidth() - 7).size() - 1) * 10;
//    }
}
