package org.ricetea.barleyteaapi.util;

import java.util.Objects;

import javax.annotation.Nonnull;

import org.bukkit.NamespacedKey;

public class NamespacedKeyUtil {

    @Nonnull
    public static final NamespacedKey EMPTY = Objects.requireNonNull(NamespacedKey.minecraft("empty"));

    public static final String BarleyTeaAPI = "barleyteaapi";

    protected NamespacedKeyUtil() {
    }

    @Nonnull
    public static NamespacedKey empty() {
        return EMPTY;
    }

    @Nonnull
    public static NamespacedKey BarleyTeaAPI(String key) {
        return new NamespacedKey(BarleyTeaAPI, key);
    }
}
