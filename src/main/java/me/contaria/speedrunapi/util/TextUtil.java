package me.contaria.speedrunapi.util;

import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public final class TextUtil {

    public static MutableText translatable(String key) {
        return new TranslatableText(key);
    }

    public static MutableText translatable(String key, Object... args) {
        return new TranslatableText(key, args);
    }

    public static MutableText literal(String string) {
        return new LiteralText(string);
    }

    public static Text empty() {
        return LiteralText.EMPTY;
    }
}
