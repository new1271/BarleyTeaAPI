package org.ricetea.utils.function;

import java.util.Objects;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface NonnullSupplier<T> {

    /**
     * Gets a result.
     *
     * @return a result
     */
    T get();

    @Nonnull
    default Supplier<T> toSupplier() {
        return this::get;
    }

    @Nonnull
    static <T> NonnullSupplier<T> fromSupplier(@Nonnull Supplier<T> supplier) {
        return () -> Objects.requireNonNull(supplier.get());
    }
}
