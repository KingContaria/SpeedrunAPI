package me.contaria.speedrunapi.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class TextUtil {

    public static MutableComponent translatable(String key) {
        return Component.translatable(key);
    }

    public static MutableComponent translatable(String key, Object... args) {
        return Component.translatable(key, args);
    }

    public static MutableComponent literal(String string) {
        return Component.translatable(string);
    }

    public static Component empty() {
        return Component.empty();
    }
}
