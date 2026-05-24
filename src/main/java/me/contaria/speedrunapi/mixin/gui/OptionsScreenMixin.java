package me.contaria.speedrunapi.mixin.gui;

import me.contaria.speedrunapi.config.screen.SpeedrunModConfigsScreen;
import me.contaria.speedrunapi.config.screen.widgets.IconButtonWidget;
import me.contaria.speedrunapi.util.IdentifierUtil;
import me.contaria.speedrunapi.util.TextUtil;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public abstract class OptionsScreenMixin extends Screen {
    @Unique
    private IconButtonWidget configButton;

    protected OptionsScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/options/OptionsScreen;repositionElements()V"))
    private void addSpeedrunConfigButton(CallbackInfo ci) {
        this.configButton = this.addRenderableWidget(new IconButtonWidget(IdentifierUtil.ofVanilla("textures/item/writable_book.png"), this.width / 2 + 159, 29, TextUtil.translatable("speedrunapi.gui.config.button"), button -> {
            assert this.minecraft != null;
            this.minecraft.setScreen(new SpeedrunModConfigsScreen(this));
        }));
    }

    @Inject(method = "repositionElements", at = @At("TAIL"))
    private void resizeSpeedrunConfigButton(CallbackInfo ci) {
        this.configButton.setPosition(this.width / 2 + 159, 29);
    }
}
