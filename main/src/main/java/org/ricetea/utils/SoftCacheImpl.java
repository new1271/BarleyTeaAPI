package org.ricetea.utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.SoftReference;
import java.util.Objects;
import java.util.function.Supplier;

class SoftCacheImpl<T> implements SoftCache<T> {

    @Nonnull
    private final Supplier<T> supplier;

    @Nullable
    private SoftReference<T> reference;

    SoftCacheImpl(@Nonnull Supplier<T> supplier) {
        this.supplier = supplier;
        reference = null;
    }

    @Nonnull
    @Override
    public Supplier<T> getSupplier() {
        return supplier;
    }

    @Nonnull
    @Override
    public T get() {
        T obj;
        SoftReference<T> reference = this.reference;
        if (reference == null) {
            obj = Objects.requireNonNull(supplier.get());
            this.reference = new SoftReference<>(obj);
        } else {
            obj = Objects.requireNonNull(reference.get());
        }
        return obj;
    }

    @Nullable
    @Override
    public T getUnsafe() {
        SoftReference<T> reference = this.reference;
        return reference == null ? null : reference.get();
    }

    @Nonnull
    @Override
    public SoftReference<T> getReference() {
        SoftReference<T> reference = this.reference;
        if (reference == null) {
            this.reference = reference = new SoftReference<>(Objects.requireNonNull(supplier.get()));
        }
        return Objects.requireNonNull(reference);
    }

    @Nullable
    @Override
    public SoftReference<T> getReferenceUnsafe() {
        return reference;
    }

    @Override
    public void reset() {
        reference = null;
    }
}
