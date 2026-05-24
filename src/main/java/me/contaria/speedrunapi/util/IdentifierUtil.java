package me.contaria.speedrunapi.util;

import net.minecraft.resources.Identifier;

public final class IdentifierUtil {

    public static Identifier of(String namespace, String path) {
        return Identifier.fromNamespaceAndPath(namespace, path);
    }

    public static Identifier ofVanilla(String path) {
        return Identifier.withDefaultNamespace(path);
    }

    public static Identifier parse(String id) {
        int index = id.indexOf(':');
        return Identifier.fromNamespaceAndPath(id.substring(0, index), id.substring(index + 1));
    }
}
