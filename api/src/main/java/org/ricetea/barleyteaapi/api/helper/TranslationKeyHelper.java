package org.ricetea.barleyteaapi.api.helper;

import org.bukkit.NamespacedKey;
import org.ricetea.utils.StringHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TranslationKeyHelper {

    @Nonnull
    public static String getTranslationKey(@Nonnull String prefix, @Nonnull NamespacedKey key) {
        return getTranslationKey(prefix, key, null);
    }

    @Nonnull
    public static String getTranslationKey(@Nonnull String prefix, @Nonnull NamespacedKey key, @Nullable String suffix) {
        return StringHelper.joinWithoutNull(".", prefix, key.getNamespace(), key.getKey(), suffix);
    }
}
