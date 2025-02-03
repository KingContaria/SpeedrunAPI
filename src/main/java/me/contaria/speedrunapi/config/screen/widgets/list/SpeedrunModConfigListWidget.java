package me.contaria.speedrunapi.config.screen.widgets.list;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import me.contaria.speedrunapi.SpeedrunAPI;
import me.contaria.speedrunapi.config.api.SpeedrunConfigScreenProvider;
import me.contaria.speedrunapi.config.screen.SpeedrunModConfigsScreen;
import me.contaria.speedrunapi.config.screen.widgets.TextWidget;
import me.contaria.speedrunapi.util.IdentifierUtil;
import me.contaria.speedrunapi.util.TextUtil;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.Person;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.Util;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@ApiStatus.Internal
public class SpeedrunModConfigListWidget extends EntryListWidget<SpeedrunModConfigListWidget.ModConfigListEntry> {
    private static final Identifier NO_MOD_ICON = IdentifierUtil.ofVanilla("textures/misc/unknown_server.png");
    private static final Identifier ERROR_HIGHLIGHTED_TEXTURE = IdentifierUtil.ofVanilla("world_list/error_highlighted");
    private static final Identifier ERROR_TEXTURE = IdentifierUtil.ofVanilla("world_list/error");
    private static final Identifier JOIN_HIGHLIGHTED_TEXTURE = IdentifierUtil.ofVanilla("world_list/join_highlighted");
    private static final Identifier JOIN_TEXTURE = IdentifierUtil.ofVanilla("world_list/join");

    private final SpeedrunModConfigsScreen parent;

    public SpeedrunModConfigListWidget(Map<ModContainer, SpeedrunConfigScreenProvider> modConfigScreenProviders, SpeedrunModConfigsScreen parent, MinecraftClient client, int width, int height, int y) {
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
        return this.getSelectedOrNull();
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }

    public abstract static class ModConfigListEntry extends EntryListWidget.Entry<ModConfigListEntry> {
    }

    public class ModEntry extends ModConfigListEntry {
        protected final ModContainer modContainer;
        protected final ModMetadata mod;
        protected final Identifier icon;
        protected final Text name;
        protected final Text version;
        @Nullable
        protected final TextWidget authors;
        protected final List<OrderedText> description;
        protected boolean hasIcon;

        public ModEntry(ModContainer mod) {
            this.modContainer = mod;
            this.mod = this.modContainer.getMetadata();
            this.icon = IdentifierUtil.of("speedrunapi", "mods/" + this.mod.getId() + "/icon");

            this.name = TextUtil.literal(this.mod.getName());
            this.version = TextUtil.literal(this.mod.getVersion().getFriendlyString().split("\\+")[0]).formatted(Formatting.GRAY);
            this.authors = this.createAuthorsText(this.mod.getAuthors());
            this.description = this.createDescription(this.mod.getDescription());

            this.registerIcon();
        }

        private @Nullable TextWidget createAuthorsText(Collection<Person> authors) {
            if (authors == null || authors.isEmpty()) {
                return null;
            }
            MutableText text = TextUtil.literal(" by ").styled(style -> style.withColor(Formatting.GRAY).withItalic(true));
            boolean shouldAddComma = false;
            for (Person person : this.mod.getAuthors()) {
                MutableText author = TextUtil.literal(person.getName());
                person.getContact().get("homepage").ifPresent(link -> author.styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link)).withFormatting(Formatting.UNDERLINE)));
                if (shouldAddComma) {
                    text.append(TextUtil.literal(", "));
                }
                text = text.append(author);
                shouldAddComma = true;
            }
            return new TextWidget(SpeedrunModConfigListWidget.this.parent, SpeedrunModConfigListWidget.this.client.textRenderer, text);
        }

        private List<OrderedText> createDescription(String description) {
            List<OrderedText> list = SpeedrunModConfigListWidget.this.client.textRenderer.wrapLines(TextUtil.literal(description), SpeedrunModConfigListWidget.this.getRowWidth() - 32 - 6);
            if (list.size() > 2) {
                return ImmutableList.of(list.get(0), OrderedText.concat(list.get(1), TextUtil.literal("...").asOrderedText()));
            }
            return list;
        }

        private void registerIcon() {
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
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            MinecraftClient client = SpeedrunModConfigListWidget.this.client;
            TextRenderer textRenderer = client.textRenderer;

            context.drawText(textRenderer, this.name, x + 32 + 3, y + 1, 0xFFFFFF, true);
            context.drawText(textRenderer, this.version, x + 32 + 3 + textRenderer.getWidth(this.name) + 4, y + 1, 0xFFFFFF, true);

            if (this.authors != null) {
                this.authors.x = x + entryWidth - this.authors.getWidth() - 5;
                this.authors.y = y + 1;
                Text hoveredComponent = this.authors.getTextComponentAtPosition(mouseX, mouseY);
                if (hoveredComponent instanceof MutableText && hoveredComponent.getStyle().getClickEvent() != null) {
                    TextColor originalColor = hoveredComponent.getStyle().getColor();
                    ((MutableText) hoveredComponent).styled(style -> style.withColor(Formatting.WHITE));
                    this.authors.render(context, mouseX, mouseY, tickDelta);
                    ((MutableText) hoveredComponent).styled(style -> style.withColor(originalColor));
                } else {
                    this.authors.render(context, mouseX, mouseY, tickDelta);
                }
            }

            int yOffset = 0;
            for (OrderedText descriptionLine : this.description) {
                context.drawText(textRenderer, descriptionLine, x + 32 + 3, y + textRenderer.fontHeight + 3 + yOffset, 0x808080, true);
                yOffset += textRenderer.fontHeight;
            }

            RenderSystem.enableBlend();
            context.drawTexture(RenderLayer::getGuiTextured, this.hasIcon ? this.icon : NO_MOD_ICON, x, y, 0.0f, 0.0f, 32, 32, 32, 32);
            RenderSystem.disableBlend();

            if (client.options.getTouchscreen().getValue() || hovered) {
                this.renderIfHovered(context, x, y, mouseX, mouseY);
            }
        }

        protected void renderIfHovered(DrawContext context, int x, int y, int mouseX, int mouseY) {
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            SpeedrunModConfigListWidget.this.setSelected(this);
            if (this.authors != null) {
                return this.authors.mouseClicked(mouseX, mouseY, button);
            }
            return false;
        }
    }

    public class ModConfigEntry extends ModEntry {
        private final SpeedrunConfigScreenProvider configScreenProvider;
        @Nullable
        private final Text unavailableTooltip;
        private long lastPress;

        public ModConfigEntry(ModContainer mod, SpeedrunConfigScreenProvider configScreenProvider) {
            super(mod);
            this.configScreenProvider = configScreenProvider;
            String configUnavailableKey = "speedrunapi.config." + this.mod.getId() + ".unavailable";
            if (Language.getInstance().hasTranslation(configUnavailableKey)) {
                this.unavailableTooltip = TextUtil.translatable(configUnavailableKey);
            } else {
                this.unavailableTooltip = TextUtil.translatable("speedrunapi.gui.config.unavailable");
            }
        }

        @Override
        protected void renderIfHovered(DrawContext context, int x, int y, int mouseX, int mouseY) {
            boolean available = this.configScreenProvider.isAvailable();
            boolean highlight = mouseX - x < 32;

            Identifier id = available ? (highlight ? JOIN_HIGHLIGHTED_TEXTURE : JOIN_TEXTURE) : (highlight ? ERROR_HIGHLIGHTED_TEXTURE : ERROR_TEXTURE);

            context.fill(x, y, x + 32, y + 32, -1601138544);
            context.drawGuiTexture(RenderLayer::getGuiTextured, id, x, y, 32, 32);

            if (!available && this.isMouseOver(mouseX, mouseY)) {
                context.drawOrderedTooltip(SpeedrunModConfigListWidget.this.client.textRenderer, SpeedrunModConfigListWidget.this.client.textRenderer.wrapLines(this.unavailableTooltip, 200), mouseX, mouseY);
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            SpeedrunModConfigListWidget.this.setSelected(this);
            if (super.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
            if (mouseX - SpeedrunModConfigListWidget.this.getRowLeft() <= 32.0) {
                return this.openConfig();
            }
            if (Util.getMeasuringTimeMs() - this.lastPress < 250L) {
                return this.openConfig();
            }
            this.lastPress = Util.getMeasuringTimeMs();
            return false;
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                return this.openConfig();
            }
            return false;
        }

        private boolean openConfig() {
            if (!this.configScreenProvider.isAvailable()) {
                return false;
            }
            SpeedrunModConfigListWidget.this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            SpeedrunModConfigListWidget.this.client.setScreen(this.configScreenProvider.createConfigScreen(SpeedrunModConfigListWidget.this.parent));
            return true;
        }
    }

    public class NoModConfigsEntry extends ModConfigListEntry {
        private final Text text = TextUtil.translatable("speedrunapi.gui.config.noConfigs");

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            context.drawCenteredTextWithShadow(SpeedrunModConfigListWidget.this.client.textRenderer, this.text, x + entryWidth / 2, y + entryHeight / 2, 0xFFFFFF);
        }
    }
}
