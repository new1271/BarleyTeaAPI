package org.ricetea.utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.SoftReference;
import java.util.function.Supplier;

public interface SoftCache<T> extends Cache<T> {

    @Nonnull
    static <T> SoftCache<T> create(@Nonnull Supplier<T> supplier) {
        return new SoftCacheImpl<>(supplier);
    }

    @Nonnull
    static <T> SoftCache<T> createThreadSafe(@Nonnull Supplier<T> supplier) {
        return new SoftCacheThreadSafeImpl<>(supplier);
    }

    @Nonnull
    SoftReference<T> getReference();

    @Nullable
    SoftReference<T> getReferenceUnsafe();
}
