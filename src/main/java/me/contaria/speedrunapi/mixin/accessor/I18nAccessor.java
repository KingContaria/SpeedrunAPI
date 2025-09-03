package me.contaria.speedrunapi.mixin.accessor;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.resource.language.TranslationStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(I18n.class)
public interface I18nAccessor {
    @Accessor("storage")
    static TranslationStorage speedrunapi$getStorage() {
        throw new UnsupportedOperationException();
    }
}
