package org.ricetea.barleyteaapi.api.helper;

import org.bukkit.NamespacedKey;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TranslationKeyHelper {

    @Nonnull
    public static String getTranslationKey(@Nonnull String prefix, @Nonnull NamespacedKey key) {
        return getTranslationKey(prefix, key, null);
    }

    @Nonnull
    public static String getTranslationKey(@Nonnull String prefix, @Nonnull NamespacedKey key, @Nullable String suffix) {
        return String.join(".", prefix, key.getNamespace(), key.getKey(), suffix);
    }
}
