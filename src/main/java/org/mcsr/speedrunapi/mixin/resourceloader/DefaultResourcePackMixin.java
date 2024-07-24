package org.mcsr.speedrunapi.mixin.resourceloader;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.resource.DefaultClientResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.mcsr.speedrunapi.SpeedrunAPI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Mixin(DefaultClientResourcePack.class)
public abstract class DefaultResourcePackMixin {

    @Unique
    private static final boolean HAS_FABRIC_RESOURCE_LOADER = FabricLoader.getInstance().isModLoaded("fabric-resource-loader-v0");

    @ModifyArg(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/resource/DefaultResourcePack;<init>([Ljava/lang/String;)V"
            )
    )
    private static String[] addModNamespaces(String[] namespaces) {
        if (HAS_FABRIC_RESOURCE_LOADER) {
            SpeedrunAPI.LOGGER.info("Disabling SpeedrunAPI resource loader in favor of fabric-resource-loader.");
            return namespaces;
        }

        List<ModContainer> mods = new ArrayList<>(FabricLoader.getInstance().getAllMods());
        mods.removeIf(mod -> mod.getMetadata().getType().equals("builtin"));

        String[] allNamespaces = new String[mods.size() + namespaces.length];
        int i;
        for (i = 0; i < namespaces.length; i++) {
            allNamespaces[i] = namespaces[i];
        }
        for (ModContainer mod : mods) {
            allNamespaces[i++] = mod.getMetadata().getId();
        }
        return allNamespaces;
    }

    @Inject(method = "findInputStream", at = @At("HEAD"), cancellable = true)
    private void loadModResources(ResourceType type, Identifier id, CallbackInfoReturnable<InputStream> cir) {
        if (HAS_FABRIC_RESOURCE_LOADER) {
            return;
        }
        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            if (mod.getMetadata().getType().equals("builtin")) {
                continue;
            }
            mod.findPath(type.getDirectory() + "/" + id.getNamespace() + "/" + id.getPath()).ifPresent(path -> {
                try {
                    cir.setReturnValue(path.toUri().toURL().openStream());
                } catch (Exception e) {
                    SpeedrunAPI.LOGGER.warn("Failed to load resource '{}' from mod '{}'.", id, mod.getMetadata().getId(), e);
                }
            });
        }
    }
}