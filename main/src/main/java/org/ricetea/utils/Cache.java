package org.ricetea.utils;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public interface Cache<T> extends Lazy<T> {
    @Nonnull
    static <T> Cache<T> create(@Nonnull Supplier<T> supplier) {
        return new CacheImpl<>(supplier);
    }

    @Nonnull
    static <T> Cache<T> createThreadSafe(@Nonnull Supplier<T> supplier) {
        return new CacheThreadSafeImpl<>(supplier);
    }

    void reset();
}
