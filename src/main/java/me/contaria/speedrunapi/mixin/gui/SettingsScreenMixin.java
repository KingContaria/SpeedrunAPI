package me.contaria.speedrunapi.mixin.gui;

import me.contaria.speedrunapi.config.screen.SpeedrunModConfigsScreen;
import me.contaria.speedrunapi.config.screen.widgets.IconButtonWidget;
import me.contaria.speedrunapi.util.IdentifierUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SettingsScreen.class)
public abstract class SettingsScreenMixin extends Screen {

    protected SettingsScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addSpeedrunConfigButton(CallbackInfo ci) {
        this.addButton(new IconButtonWidget(IdentifierUtil.ofVanilla("textures/item/writable_book.png"), this.width / 2 + 160, this.height / 6 - 12, I18n.translate("speedrunapi.gui.config.button"), button -> {
            assert this.minecraft != null;
            this.minecraft.openScreen(new SpeedrunModConfigsScreen(this));
        }));
    }
}
