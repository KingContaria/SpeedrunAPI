package org.mcsr.speedrunapi.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.mcsr.speedrunapi.config.screen.SpeedrunModConfigsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public abstract class OptionsScreenMixin extends Screen {

    @Unique
    private static final Identifier SPEEDRUNAPI$WRITABLE_BOOK = new Identifier("textures/item/writable_book.png");

    @Unique
    private ButtonWidget speedrunAPI$speedrunConfigButton;

    protected OptionsScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void speedrunAPI$addSpeedrunConfigButton(CallbackInfo ci) {
        this.speedrunAPI$speedrunConfigButton = this.addButton(new ButtonWidget(this.width / 2 + 160, this.height / 6 - 12, 20, 20, LiteralText.EMPTY, button -> {
            assert this.client != null;
            this.client.openScreen(new SpeedrunModConfigsScreen(this));
        }));
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void speedrunAPI$renderSpeedrunConfigButtonOverlay(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        assert this.client != null;
        this.client.getTextureManager().bindTexture(SPEEDRUNAPI$WRITABLE_BOOK);
        ButtonWidget button = this.speedrunAPI$speedrunConfigButton;
        drawTexture(matrices, button.x + 2, button.y + 2, 0.0F, 0.0F, 16, 16, 16, 16);
        if (button.isHovered()) {
            this.drawCenteredText(matrices, this.textRenderer, new TranslatableText("speedrunapi.gui.speedrunConfigButton"), button.x + button.getWidth() / 2, button.y - 15, 16777215);
        }
    }
}
