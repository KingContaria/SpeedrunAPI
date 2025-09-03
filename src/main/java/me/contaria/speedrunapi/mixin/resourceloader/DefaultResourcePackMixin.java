package me.contaria.speedrunapi.mixin.resourceloader;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.contaria.speedrunapi.SpeedrunAPI;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Stream;

@Mixin(DefaultResourcePack.class)
public abstract class DefaultResourcePackMixin {
    @Shadow
    @Final
    public static Set<String> NAMESPACES;

    @Unique
    private static final boolean HAS_FABRIC_RESOURCE_LOADER = FabricLoader.getInstance().isModLoaded("fabric-resource-loader-v0");
    @Unique
    private static final Map<String, Set<ModContainer>> NAMESPACES_TO_MODS = new HashMap<>();
    @Unique
    private static final Set<String> NAMESPACES_INCLUDING_MODS = new HashSet<>();

    static {
        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            if (mod.getMetadata().getType().equals("builtin")) {
                continue;
            }
            mod.findPath("assets").filter(Files::isDirectory).ifPresent(assets -> {
                Set<String> namespaces = new HashSet<>();
                try (Stream<Path> stream = Files.list(assets)) {
                    stream.filter(Files::isDirectory)
                            .map(path -> path.getFileName().toString().replaceAll(Matcher.quoteReplacement("[/\\]"), ""))
                            .filter(namespace -> !namespace.equals("minecraft") && !namespace.equals("realms"))
                            .forEach(namespaces::add);
                } catch (IOException e) {
                    SpeedrunAPI.LOGGER.error("SpeedrunAPI failed to check resources for mod: {}", mod.getMetadata().getId());
                }

                for (String namespace : namespaces) {
                    NAMESPACES_TO_MODS.computeIfAbsent(namespace, key -> new TreeSet<>(Comparator.comparing(m -> m.getMetadata().getId(), String::compareTo))).add(mod);
                }
            });
        }

        NAMESPACES_INCLUDING_MODS.addAll(NAMESPACES);
        NAMESPACES_INCLUDING_MODS.addAll(NAMESPACES_TO_MODS.keySet());
    }

    @ModifyReturnValue(
            method = "getNamespaces",
            at = @At("RETURN")
    )
    private Set<String> includeModNamespaces(Set<String> namespaces) {
        return NAMESPACES_INCLUDING_MODS;
    }

    @Inject(method = "open", at = @At("HEAD"), cancellable = true)
    private void loadModResources(Identifier id, CallbackInfoReturnable<InputStream> cir) {
        if (HAS_FABRIC_RESOURCE_LOADER) {
            return;
        }
        Set<ModContainer> mods = NAMESPACES_TO_MODS.get(id.getNamespace());
        if (mods == null) {
            return;
        }
        for (ModContainer mod : mods) {
            mod.findPath("assets/" + id.getNamespace() + "/" + id.getPath()).ifPresent(path -> {
                try {
                    cir.setReturnValue(path.toUri().toURL().openStream());
                } catch (Exception e) {
                    SpeedrunAPI.LOGGER.warn("Failed to load resource '{}' from mod '{}'.", id, mod.getMetadata().getId(), e);
                }
            });
        }
    }
}