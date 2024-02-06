package org.ricetea.utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

public interface Lazy<T> extends Supplier<T> {

    @Nonnull
    static <T> Lazy<T> create(@Nonnull Supplier<T> supplier) {
        return new LazyImpl<>(supplier);
    }

    @Nonnull
    static <T> Lazy<T> createThreadSafe(@Nonnull Supplier<T> supplier) {
        return new LazyThreadSafeImpl<>(supplier);
    }

    @Nonnull
    Supplier<T> getSupplier();

    @Nonnull
    T get();

    @Nullable
    T getUnsafe();
}
