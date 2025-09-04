package me.contaria.speedrunapi.mixin.gui;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.contaria.speedrunapi.config.screen.AbstractSpeedrunConfigAPIScreen;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @ModifyExpressionValue(
            method = "getMaxFramerate",
            at = @At(
                    value = "CONSTANT",
                    args = "intValue=30"
            )
    )
    private int increaseMaxFramerateOnSpeedrunAPIScreens(int maxFramerate) {
        if (MinecraftClient.getInstance().currentScreen instanceof AbstractSpeedrunConfigAPIScreen) {
            return 60;
        }
        return maxFramerate;
    }
}
