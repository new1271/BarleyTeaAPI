package org.ricetea.barleyteaapi.util;

import org.bukkit.NamespacedKey;
import org.ricetea.utils.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NamespacedKeyUtil {
    public static final String BarleyTeaAPI = "barleyteaapi";

    protected NamespacedKeyUtil() {
    }

    @Nonnull
    public static NamespacedKey empty() {
        return Constants.EMPTY_NAMESPACED_KEY;
    }

    public static boolean isEmpty(@Nullable NamespacedKey key) {
        return key == null || empty().equals(key);
    }

    @Nonnull
    public static NamespacedKey BarleyTeaAPI(String key) {
        return new NamespacedKey(BarleyTeaAPI, key);
    }
}
