package org.ricetea.barleyteaapi.util;

import org.bukkit.NamespacedKey;

import javax.annotation.Nonnull;
import java.util.Objects;

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
