package org.mcsr.speedrunapi.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.OptionsScreen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.mcsr.speedrunapi.config.screen.SpeedrunModConfigsScreen;
import org.mcsr.speedrunapi.config.screen.widgets.IconButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public abstract class OptionsScreenMixin extends Screen {

    @Unique
    private static final Identifier SPEEDRUNAPI$WRITABLE_BOOK = new Identifier("textures/item/writable_book.png");

    protected OptionsScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addSpeedrunConfigButton(CallbackInfo ci) {
        this.addButton(new IconButtonWidget(SPEEDRUNAPI$WRITABLE_BOOK, this.width / 2 + 160, this.height / 6 - 12, new TranslatableText("speedrunapi.gui.speedrunConfigButton"), button -> {
            assert this.client != null;
            this.client.openScreen(new SpeedrunModConfigsScreen(this));
        }));
    }
}
