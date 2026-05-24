package me.contaria.speedrunapi.config.screen.widgets.list;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.NativeImage;
import me.contaria.speedrunapi.SpeedrunAPI;
import me.contaria.speedrunapi.config.api.SpeedrunConfigScreenProvider;
import me.contaria.speedrunapi.config.screen.SpeedrunModConfigsScreen;
import me.contaria.speedrunapi.config.screen.widgets.TextWidget;
import me.contaria.speedrunapi.util.IdentifierUtil;
import me.contaria.speedrunapi.util.TextUtil;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.Person;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Util;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@ApiStatus.Internal
public class SpeedrunModConfigListWidget extends AbstractSelectionList<SpeedrunModConfigListWidget.ModConfigListEntry> {
    private static final Identifier NO_MOD_ICON = IdentifierUtil.ofVanilla("textures/misc/unknown_server.png");
    private static final Identifier ERROR_HIGHLIGHTED_TEXTURE = IdentifierUtil.ofVanilla("world_list/error_highlighted");
    private static final Identifier ERROR_TEXTURE = IdentifierUtil.ofVanilla("world_list/error");
    private static final Identifier JOIN_HIGHLIGHTED_TEXTURE = IdentifierUtil.ofVanilla("world_list/join_highlighted");
    private static final Identifier JOIN_TEXTURE = IdentifierUtil.ofVanilla("world_list/join");

    private final SpeedrunModConfigsScreen parent;

    public SpeedrunModConfigListWidget(Map<ModContainer, SpeedrunConfigScreenProvider> modConfigScreenProviders, SpeedrunModConfigsScreen parent, Minecraft client, int width, int height, int y) {
        super(client, width, height, y, 36);
        this.parent = parent;

        for (Map.Entry<ModContainer, SpeedrunConfigScreenProvider> config : modConfigScreenProviders.entrySet()) {
            this.addEntry(new ModConfigEntry(config.getKey(), config.getValue()));
        }
/*
        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            // the mod.getContainingMod().isPresent() check fails in a dev environment
            if (modConfigScreenProviders.containsKey(mod) || mod.getMetadata().getType().equals("builtin") || mod.getMetadata().getId().equals("fabricloader") || mod.getContainingMod().isPresent()) {
                continue;
            }
            this.addEntry(new ModEntry(mod));
        }

 */

        if (this.children().isEmpty()) {
            this.addEntry(new NoModConfigsEntry());
        }
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 50;
    }

    @Override
    public ModConfigListEntry getFocused() {
        return this.getSelected();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {
    }

    public abstract static class ModConfigListEntry extends AbstractSelectionList.Entry<ModConfigListEntry> {
    }

    public class ModEntry extends ModConfigListEntry {
        protected final ModContainer modContainer;
        protected final ModMetadata mod;
        protected final Identifier icon;
        protected final Component name;
        protected final Component version;
        @Nullable
        protected final TextWidget authors;
        protected final List<FormattedCharSequence> description;
        protected boolean hasIcon;

        public ModEntry(ModContainer mod) {
            this.modContainer = mod;
            this.mod = this.modContainer.getMetadata();
            this.icon = IdentifierUtil.of("speedrunapi", "mods/" + this.mod.getId() + "/icon");

            this.name = TextUtil.literal(this.mod.getName());
            this.version = TextUtil.literal(this.mod.getVersion().getFriendlyString().split("\\+")[0]).withStyle(ChatFormatting.GRAY);
            this.authors = this.createAuthorsText(this.mod.getAuthors());
            this.description = this.createDescription(this.mod.getDescription());

            this.registerIcon();
        }

        private @Nullable TextWidget createAuthorsText(Collection<Person> authors) {
            if (authors == null || authors.isEmpty()) {
                return null;
            }
            MutableComponent text = TextUtil.literal(" by ").withStyle(style -> style.withColor(ChatFormatting.GRAY).withItalic(true));
            boolean shouldAddComma = false;
            for (Person person : this.mod.getAuthors()) {
                MutableComponent author = TextUtil.literal(person.getName());
                person.getContact().get("homepage").ifPresent(link -> {
                    try {
                        URI uri = new URI(link);
                        author.withStyle(style -> style.withClickEvent(new ClickEvent.OpenUrl(uri)).withUnderlined(true));
                    } catch (URISyntaxException ignored) {
                    }
                });
                if (shouldAddComma) {
                    text.append(TextUtil.literal(", "));
                }
                text = text.append(author);
                shouldAddComma = true;
            }
            return new TextWidget(SpeedrunModConfigListWidget.this.parent, SpeedrunModConfigListWidget.this.minecraft.font, text);
        }

        private List<FormattedCharSequence> createDescription(String description) {
            List<FormattedCharSequence> list = SpeedrunModConfigListWidget.this.minecraft.font.split(TextUtil.literal(description), SpeedrunModConfigListWidget.this.getRowWidth() - 32 - 6);
            if (list.size() > 2) {
                return ImmutableList.of(list.get(0), FormattedCharSequence.composite(list.get(1), TextUtil.literal("...").getVisualOrderText()));
            }
            return list;
        }

        private void registerIcon() {
            this.mod.getIconPath(32).flatMap(this.modContainer::findPath).ifPresent(iconPath -> {
                try (InputStream inputStream = Files.newInputStream(iconPath)) {
                    SpeedrunModConfigListWidget.this.minecraft.getTextureManager().register(this.icon, new DynamicTexture(iconPath::toString, NativeImage.read(inputStream)));
                    this.hasIcon = true;
                } catch (IOException e) {
                    SpeedrunAPI.LOGGER.warn("Failed to load mod icon for {}.", this.mod.getId(), e);
                }
            });
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
            Minecraft client = SpeedrunModConfigListWidget.this.minecraft;
            Font textRenderer = client.font;

            graphics.text(textRenderer, this.name, this.getContentX() + 32 + 3, this.getContentY() + 1, 0xFFFFFFff, false);
            graphics.text(textRenderer, this.version, this.getContentX() + 32 + 3 + textRenderer.width(this.name) + 4, this.getContentY() + 1, 0xFFFFFFff, false);

            if (this.authors != null) {
                this.authors.x = this.getContentX() + this.getContentWidth() - this.authors.getWidth() - 5;
                this.authors.y = this.getContentY() + 1;
                Component hoveredComponent = this.authors.getTextComponentAtPosition(mouseX, mouseY);
                if (hoveredComponent instanceof MutableComponent && hoveredComponent.getStyle().getClickEvent() != null) {
                    TextColor originalColor = hoveredComponent.getStyle().getColor();
                    ((MutableComponent) hoveredComponent).withStyle(style -> style.withColor(ChatFormatting.WHITE));
                    this.authors.extractRenderState(graphics, mouseX, mouseY, a);
                    ((MutableComponent) hoveredComponent).withStyle(style -> style.withColor(originalColor));
                } else {
                    this.authors.extractRenderState(graphics, mouseX, mouseY, a);
                }
            }

            int yOffset = 0;
            for (FormattedCharSequence descriptionLine : this.description) {
                graphics.text(textRenderer, descriptionLine, this.getContentX() + 32 + 3, this.getContentY() + textRenderer.lineHeight + 3 + yOffset, 0xff808080, false);
                yOffset += textRenderer.lineHeight;
            }

            graphics.blit(RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA, this.hasIcon ? this.icon : NO_MOD_ICON, this.getContentX(), this.getContentY(), 0.0f, 0.0f, 32, 32, 32, 32);

            if (client.options.touchscreen().get() || hovered) {
                this.renderIfHovered(graphics, mouseX, mouseY);
            }
        }

        protected void renderIfHovered(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            SpeedrunModConfigListWidget.this.setSelected(this);
            if (this.authors != null) {
                return this.authors.mouseClicked(event, doubleClick);
            }
            return false;
        }
    }

    public class ModConfigEntry extends ModEntry {
        private final SpeedrunConfigScreenProvider configScreenProvider;
        @Nullable
        private final Component unavailableTooltip;
        private long lastPress;

        public ModConfigEntry(ModContainer mod, SpeedrunConfigScreenProvider configScreenProvider) {
            super(mod);
            this.configScreenProvider = configScreenProvider;
            String configUnavailableKey = "speedrunapi.config." + this.mod.getId() + ".unavailable";
            if (Language.getInstance().has(configUnavailableKey)) {
                this.unavailableTooltip = TextUtil.translatable(configUnavailableKey);
            } else {
                this.unavailableTooltip = TextUtil.translatable("speedrunapi.gui.config.unavailable");
            }
        }

        @Override
        protected void renderIfHovered(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
            boolean available = this.configScreenProvider.isAvailable();
            boolean highlight = mouseX - this.getContentX() < 32;

            Identifier id = available ? (highlight ? JOIN_HIGHLIGHTED_TEXTURE : JOIN_TEXTURE) : (highlight ? ERROR_HIGHLIGHTED_TEXTURE : ERROR_TEXTURE);

            graphics.fill(this.getContentX(), this.getContentY(), this.getContentX() + 32, this.getContentY() + 32, -1601138544);
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, id, this.getContentX(), this.getContentY(), 32, 32);

            if (!available && this.isMouseOver(mouseX, mouseY)) {
                graphics.setTooltipForNextFrame(SpeedrunModConfigListWidget.this.minecraft.font, SpeedrunModConfigListWidget.this.minecraft.font.split(this.unavailableTooltip, 200), mouseX, mouseY);
            }
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            SpeedrunModConfigListWidget.this.setSelected(this);
            if (super.mouseClicked(event, doubleClick)) {
                return true;
            }
            if (event.x() - SpeedrunModConfigListWidget.this.getRowLeft() <= 32.0) {
                return this.openConfig();
            }
            if (Util.getMillis() - this.lastPress < 250L) {
                return this.openConfig();
            }
            this.lastPress = Util.getMillis();
            return false;
        }

        @Override
        public boolean keyPressed(KeyEvent event) {
            if (event.key() == GLFW.GLFW_KEY_ENTER || event.key() == GLFW.GLFW_KEY_KP_ENTER) {
                return this.openConfig();
            }
            return false;
        }

        private boolean openConfig() {
            if (!this.configScreenProvider.isAvailable()) {
                return false;
            }
            SpeedrunModConfigListWidget.this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            SpeedrunModConfigListWidget.this.minecraft.setScreen(this.configScreenProvider.createConfigScreen(SpeedrunModConfigListWidget.this.parent));
            return true;
        }
    }

    public class NoModConfigsEntry extends ModConfigListEntry {
        private final Component text = TextUtil.translatable("speedrunapi.gui.config.noConfigs");

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
            graphics.centeredText(SpeedrunModConfigListWidget.this.minecraft.font, this.text, this.getContentX() + this.getContentWidth() / 2, this.getContentY() + this.getContentHeight() / 2, 0xFFFFFFff);
        }
    }
}
