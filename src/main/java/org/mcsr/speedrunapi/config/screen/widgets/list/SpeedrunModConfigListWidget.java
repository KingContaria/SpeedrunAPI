package org.mcsr.speedrunapi.config.screen.widgets.list;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;
import org.mcsr.speedrunapi.SpeedrunAPI;
import org.mcsr.speedrunapi.config.api.SpeedrunConfigScreenProvider;
import org.mcsr.speedrunapi.config.screen.SpeedrunModConfigsScreen;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;

public class SpeedrunModConfigListWidget extends EntryListWidget<SpeedrunModConfigListWidget.ModConfigEntry> {

    private static final Identifier NO_MOD_ICON = new Identifier("textures/misc/unknown_server.png");
    private static final Identifier EDIT_MOD_CONFIG = new Identifier("textures/gui/world_selection.png");

    private final SpeedrunModConfigsScreen parent;

    public SpeedrunModConfigListWidget(Map<ModContainer, SpeedrunConfigScreenProvider> modConfigScreenProviders, SpeedrunModConfigsScreen parent, MinecraftClient client, int width, int height, int top, int bottom) {
        super(client, width, height, top, bottom, 36);
        this.parent = parent;

        List<ModContainer> sortedModList = new ArrayList<>(modConfigScreenProviders.keySet());
        sortedModList.sort(Comparator.comparing(mod -> mod.getMetadata().getId()));
        for (ModContainer mod : sortedModList) {
            this.addEntry(new ModConfigEntry(mod, modConfigScreenProviders.get(mod)));
        }
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 50;
    }

    @Override
    protected int getScrollbarPositionX() {
        return super.getScrollbarPositionX() + 20;
    }

    @Override
    public ModConfigEntry getFocused() {
        return this.getSelected();
    }

    public class ModConfigEntry extends EntryListWidget.Entry<ModConfigEntry> {

        private final ModContainer modContainer;
        private final ModMetadata mod;
        private final SpeedrunConfigScreenProvider configScreenProvider;
        private final Identifier icon;
        private boolean hasIcon;
        private long lastPress;

        public ModConfigEntry(ModContainer mod, SpeedrunConfigScreenProvider configScreenProvider) {
            this.modContainer = mod;
            this.mod = this.modContainer.getMetadata();
            this.configScreenProvider = configScreenProvider;
            this.icon = new Identifier("speedrunapi", "mods/" + this.mod.getId() + "/icon");

            this.registerIcon();
        }

        private void registerIcon() {
            if (SpeedrunModConfigListWidget.this.client.getTextureManager().getTexture(this.icon) != null) {
                this.hasIcon = true;
                return;
            }
            this.mod.getIconPath(32).flatMap(this.modContainer::findPath).ifPresent(iconPath -> {
                try (InputStream inputStream = Files.newInputStream(iconPath)) {
                    SpeedrunModConfigListWidget.this.client.getTextureManager().registerTexture(this.icon, new NativeImageBackedTexture(NativeImage.read(inputStream)));
                    this.hasIcon = true;
                } catch (IOException e) {
                    SpeedrunAPI.LOGGER.warn("Failed to load mod icon for {}.", this.mod.getId(), e);
                }
            });
        }

        @Override
        @SuppressWarnings("deprecation")
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            Text name = new LiteralText(this.mod.getName());
            Text description = new LiteralText(this.mod.getDescription());

            MinecraftClient client = SpeedrunModConfigListWidget.this.client;
            TextRenderer textRenderer = client.textRenderer;

            textRenderer.draw(matrices, name, (float)(x + 32 + 3), (float)(y + 1), 0xFFFFFF);
            textRenderer.drawTrimmed(description, (x + 32 + 3), (y + textRenderer.fontHeight + 3), entryWidth - 32 - 6, 0x808080);

            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);

            client.getTextureManager().bindTexture(this.hasIcon ? this.icon : NO_MOD_ICON);
            RenderSystem.enableBlend();
            DrawableHelper.drawTexture(matrices, x, y, 0.0f, 0.0f, 32, 32, 32, 32);
            RenderSystem.disableBlend();
            if (client.options.touchscreen || hovered) {
                client.getTextureManager().bindTexture(EDIT_MOD_CONFIG);
                DrawableHelper.fill(matrices, x, y, x + 32, y + 32, -1601138544);
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                int textureOffset = mouseX - x < 32 ? 32 : 0;
                DrawableHelper.drawTexture(matrices, x, y, 0.0f, textureOffset, 32, 32, 256, 256);
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            SpeedrunModConfigListWidget.this.setSelected(this);
            if (mouseX - SpeedrunModConfigListWidget.this.getRowLeft() <= 32.0) {
                this.openConfig();
                return true;
            }
            if (Util.getMeasuringTimeMs() - this.lastPress < 250L) {
                this.openConfig();
                return true;
            }
            this.lastPress = Util.getMeasuringTimeMs();
            return false;
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                this.openConfig();
                return true;
            }
            return false;
        }

        private void openConfig() {
            SpeedrunModConfigListWidget.this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            SpeedrunModConfigListWidget.this.client.openScreen(this.configScreenProvider.createConfigScreen(SpeedrunModConfigListWidget.this.parent));
        }
    }
}
