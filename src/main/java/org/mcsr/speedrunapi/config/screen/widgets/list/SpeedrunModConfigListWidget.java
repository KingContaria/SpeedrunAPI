package org.mcsr.speedrunapi.config.screen.widgets.list;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.Person;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;
import org.mcsr.speedrunapi.SpeedrunAPI;
import org.mcsr.speedrunapi.config.api.SpeedrunConfigScreenProvider;
import org.mcsr.speedrunapi.config.screen.SpeedrunModConfigsScreen;
import org.mcsr.speedrunapi.config.screen.widgets.TextWidget;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;

public class SpeedrunModConfigListWidget extends EntryListWidget<SpeedrunModConfigListWidget.ModConfigListEntry> {

    private static final Identifier NO_MOD_ICON = new Identifier("textures/misc/unknown_server.png");
    private static final Identifier EDIT_MOD_CONFIG = new Identifier("textures/gui/world_selection.png");

    private final SpeedrunModConfigsScreen parent;

    public SpeedrunModConfigListWidget(Map<ModContainer, SpeedrunConfigScreenProvider> modConfigScreenProviders, SpeedrunModConfigsScreen parent, MinecraftClient client, int width, int height, int top, int bottom) {
        super(client, width, height, top, bottom, 36);
        this.parent = parent;

        for (Map.Entry<ModContainer, SpeedrunConfigScreenProvider> modConfig : modConfigScreenProviders.entrySet()) {
            if (!modConfig.getValue().isAvailable()) {
                continue;
            }
            this.addEntry(new ModConfigEntry(modConfig.getKey(), modConfig.getValue()));
        }
        if (this.children().isEmpty()) {
            this.addEntry(new NoModConfigsEntry());
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
    public ModConfigListEntry getFocused() {
        return this.getSelected();
    }

    public abstract static class ModConfigListEntry extends EntryListWidget.Entry<ModConfigListEntry> {
    }

    public class ModConfigEntry extends ModConfigListEntry {

        private final ModContainer modContainer;
        private final ModMetadata mod;
        private final SpeedrunConfigScreenProvider configScreenProvider;
        private final Identifier icon;
        private final TextWidget name;
        private final TextWidget authors;
        private final Text description;
        private boolean hasIcon;
        private long lastPress;

        public ModConfigEntry(ModContainer mod, SpeedrunConfigScreenProvider configScreenProvider) {
            this.modContainer = mod;
            this.mod = this.modContainer.getMetadata();
            this.configScreenProvider = configScreenProvider;
            this.icon = new Identifier("speedrunapi", "mods/" + this.mod.getId() + "/icon");

            this.name = new TextWidget(SpeedrunModConfigListWidget.this.parent, SpeedrunModConfigListWidget.this.client.textRenderer, new LiteralText(this.mod.getName()));
            MutableText authors = new LiteralText(" by ").styled(style -> style.withColor(Formatting.GRAY).withItalic(true));
            boolean shouldAddComma = false;
            for (Person person : this.mod.getAuthors()) {
                LiteralText author = new LiteralText(person.getName());
                person.getContact().get("homepage").ifPresent(link -> author.styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link)).withFormatting(Formatting.UNDERLINE)));
                if (shouldAddComma) {
                    authors.append(new LiteralText(", "));
                }
                authors = authors.append(author);
                shouldAddComma = true;
            }
            this.authors = new TextWidget(SpeedrunModConfigListWidget.this.parent, SpeedrunModConfigListWidget.this.client.textRenderer, authors);

            this.description = new LiteralText(this.mod.getDescription());

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
            MinecraftClient client = SpeedrunModConfigListWidget.this.client;
            TextRenderer textRenderer = client.textRenderer;

            this.name.x = x + 32 + 3;
            this.name.y = y + 1;
            this.name.render(matrices, mouseX, mouseY, tickDelta);

            this.authors.x = x + entryWidth - this.authors.getWidth() - 5;
            this.authors.y = y + 1;
            Text hoveredComponent = this.authors.getTextComponentAtPosition(mouseX, mouseY);
            if (hoveredComponent instanceof MutableText && hoveredComponent.getStyle().getClickEvent() != null) {
                TextColor originalColor = hoveredComponent.getStyle().getColor();
                ((MutableText) hoveredComponent).styled(style -> style.withColor(Formatting.WHITE));
                this.authors.render(matrices, mouseX, mouseY, tickDelta);
                ((MutableText) hoveredComponent).styled(style -> style.withColor(originalColor));
            } else {
                this.authors.render(matrices, mouseX, mouseY, tickDelta);
            }

            textRenderer.drawTrimmed(this.description, (x + 32 + 3), (y + textRenderer.fontHeight + 3), entryWidth - 32 - 6, 0x808080);

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
            if (this.authors.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
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

    public class NoModConfigsEntry extends ModConfigListEntry {

        private final Text text = new TranslatableText("speedrunapi.gui.speedrunConfig.noConfig");

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            SpeedrunModConfigListWidget.this.drawCenteredText(matrices, SpeedrunModConfigListWidget.this.client.textRenderer, this.text, x + entryWidth / 2, y + entryHeight / 2, 0xFFFFFF);
        }
    }
}
