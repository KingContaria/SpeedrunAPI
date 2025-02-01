package me.contaria.speedrunapi.mixin.gui;

import me.contaria.speedrunapi.config.screen.SpeedrunModConfigsScreen;
import me.contaria.speedrunapi.config.screen.widgets.IconButtonWidget;
import me.contaria.speedrunapi.util.IdentifierUtil;
import me.contaria.speedrunapi.util.TextUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public abstract class OptionsScreenMixin extends Screen {

    protected OptionsScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addSpeedrunConfigButton(CallbackInfo ci) {
        this.addDrawableChild(new IconButtonWidget(IdentifierUtil.ofVanilla("textures/item/writable_book.png"), this.width / 2 + 160, this.height / 6 - 12, TextUtil.translatable("speedrunapi.gui.config.button"), button -> {
            assert this.client != null;
            this.client.setScreen(new SpeedrunModConfigsScreen(this));
        }));
    }
}
