package me.contaria.speedrunapi.mixin.entrypoint;

import me.contaria.speedrunapi.config.SpeedrunConfigAPI;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onPostLaunchEntrypoint(CallbackInfo ci) {
        SpeedrunConfigAPI.onPostLaunch();
    }
}
