package org.mcsr.speedrunapi.mixin_plugin;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * A MixinConfigPlugin that can be used by mods directly, or extended to build on added functionality.
 * <p>
 * Current features are:
 * <p>
 * - Automatically disables mixins in the "the.mods.mixinPackage.compat.modid" package if the targeted mod isn't present.
 */
public class SpeedrunMixinConfigPlugin implements IMixinConfigPlugin {
    protected String mixinPackage;

    @Override
    public void onLoad(String mixinPackage) {
        this.mixinPackage = mixinPackage;
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String compatPackage = this.mixinPackage + ".compat.";
        if (mixinClassName.startsWith(compatPackage)) {
            String mod = mixinClassName.replaceFirst(compatPackage, "").split("\\.", 2)[0];
            return FabricLoader.getInstance().isModLoaded(mod);
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
