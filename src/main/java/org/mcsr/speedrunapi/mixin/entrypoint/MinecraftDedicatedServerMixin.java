package org.mcsr.speedrunapi.mixin.entrypoint;

import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import org.mcsr.speedrunapi.config.SpeedrunConfigAPI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftDedicatedServer.class)
public abstract class MinecraftDedicatedServerMixin {

    @Inject(method = "setupServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/dedicated/MinecraftDedicatedServer;loadWorld()V"))
    private void onPostLaunchEntrypoint(CallbackInfoReturnable<Boolean> cir) {
        SpeedrunConfigAPI.onPostLaunch();
    }
}
