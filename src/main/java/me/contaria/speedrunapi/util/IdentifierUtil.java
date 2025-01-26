package me.contaria.speedrunapi.util;

import net.minecraft.util.Identifier;

public final class IdentifierUtil {

    public static Identifier of(String namespace, String path) {
        return new Identifier(namespace, path);
    }

    public static Identifier ofVanilla(String path) {
        return new Identifier("minecraft", path);
    }

    public static Identifier parse(String id) {
        int index = id.indexOf(':');
        return new Identifier(id.substring(0, index), id.substring(index + 1));
    }
}
