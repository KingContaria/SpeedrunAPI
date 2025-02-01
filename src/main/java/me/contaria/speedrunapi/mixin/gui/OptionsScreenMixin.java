package me.contaria.speedrunapi.mixin.gui;

import me.contaria.speedrunapi.config.screen.SpeedrunModConfigsScreen;
import me.contaria.speedrunapi.config.screen.widgets.IconButtonWidget;
import me.contaria.speedrunapi.util.IdentifierUtil;
import me.contaria.speedrunapi.util.TextUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public abstract class OptionsScreenMixin extends Screen {
    @Unique
    private IconButtonWidget configButton;

    protected OptionsScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/option/OptionsScreen;refreshWidgetPositions()V"))
    private void addSpeedrunConfigButton(CallbackInfo ci) {
        this.configButton = this.addDrawableChild(new IconButtonWidget(IdentifierUtil.ofVanilla("textures/item/writable_book.png"), this.width / 2 + 159, 29, TextUtil.translatable("speedrunapi.gui.config.button"), button -> {
            assert this.client != null;
            this.client.setScreen(new SpeedrunModConfigsScreen(this));
        }));
    }

    @Inject(method = "refreshWidgetPositions", at = @At("TAIL"))
    private void resizeSpeedrunConfigButton(CallbackInfo ci) {
        this.configButton.setPosition(this.width / 2 + 159, 29);
    }
}
