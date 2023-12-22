package org.ricetea.utils.function;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Supplier;

@FunctionalInterface
public interface NonnullSupplier<T> {

    @Nonnull
    static <T> NonnullSupplier<T> fromSupplier(@Nonnull Supplier<T> supplier) {
        return () -> Objects.requireNonNull(supplier.get());
    }

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
}
