package me.contaria.speedrunapi.mixin.gui;

import me.contaria.speedrunapi.config.api.gui.ButtonWidgetCallback;
import me.contaria.speedrunapi.config.screen.SpeedrunModConfigsScreen;
import me.contaria.speedrunapi.config.screen.widgets.IconButtonWidget;
import me.contaria.speedrunapi.util.IdentifierUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SettingsScreen.class)
public abstract class SettingsScreenMixin extends Screen {

    @Inject(method = "init", at = @At("TAIL"))
    private void addSpeedrunConfigButton(CallbackInfo ci) {
        this.buttons.add(new IconButtonWidget(436743, IdentifierUtil.ofVanilla("textures/items/book_writable.png"), this.width / 2 + 160, this.height / 6 - 12, I18n.translate("speedrunapi.gui.config.button"), button -> {
            this.client.setScreen(new SpeedrunModConfigsScreen(this));
        }));
    }

    @Inject(
            method = "buttonClicked",
            at = @At("TAIL")
    )
    private void onPress(ButtonWidget button, CallbackInfo ci) {
        if (button instanceof ButtonWidgetCallback) {
            ((ButtonWidgetCallback) button).onPress();
        }
    }
}
