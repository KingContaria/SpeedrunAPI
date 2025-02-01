package me.contaria.speedrunapi.util;

import net.minecraft.util.Identifier;

public final class IdentifierUtil {

    public static Identifier of(String namespace, String path) {
        return Identifier.of(namespace, path);
    }

    public static Identifier ofVanilla(String path) {
        return Identifier.ofVanilla(path);
    }

    public static Identifier parse(String id) {
        int index = id.indexOf(':');
        return Identifier.of(id.substring(0, index), id.substring(index + 1));
    }
}
