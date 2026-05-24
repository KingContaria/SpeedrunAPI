package me.contaria.speedrunapi.mixin.entrypoint;

import me.contaria.speedrunapi.config.SpeedrunConfigAPI;
import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DedicatedServer.class)
public abstract class DedicatedServerMixin {

    @Inject(method = "initServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/dedicated/DedicatedServer;loadLevel()V"))
    private void onPostLaunchEntrypoint(CallbackInfoReturnable<Boolean> cir) {
        SpeedrunConfigAPI.onPostLaunch();
    }
}
