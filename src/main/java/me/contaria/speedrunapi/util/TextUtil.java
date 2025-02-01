package me.contaria.speedrunapi.util;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public final class TextUtil {

    public static MutableText translatable(String key) {
        return Text.translatable(key);
    }

    public static MutableText translatable(String key, Object... args) {
        return Text.translatable(key, args);
    }

    public static MutableText literal(String string) {
        return Text.translatable(string);
    }

    public static Text empty() {
        return Text.empty();
    }
}
