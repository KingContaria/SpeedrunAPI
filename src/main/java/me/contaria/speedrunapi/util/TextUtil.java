package me.contaria.speedrunapi.util;

import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public final class TextUtil {

    public static Text translatable(String key) {
        return new TranslatableText(key);
    }

    public static Text translatable(String key, Object... args) {
        return new TranslatableText(key, args);
    }

    public static Text literal(String string) {
        return new LiteralText(string);
    }

    public static Text empty() {
        return new LiteralText("");
    }
}
