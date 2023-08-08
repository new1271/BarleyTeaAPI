package org.ricetea.barleyteaapi.util;

import javax.annotation.Nonnull;

import org.bukkit.NamespacedKey;

public final class NamespacedKeyUtils {

    public static final String Namespace = "barleyteaapi";

    private NamespacedKeyUtils() {
    }

    @Nonnull
    public static NamespacedKey BarleyTeaAPI(String key) {
        return new NamespacedKey(Namespace, key);
    }
}
