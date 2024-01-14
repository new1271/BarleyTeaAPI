package org.ricetea.utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Supplier;

class LazyThreadSafeImpl<T> implements Lazy<T> {

    @Nonnull
    private final Supplier<T> supplier;

    @Nonnull
    protected final Object syncRoot = new Object();

    @Nullable
    protected T obj;

    LazyThreadSafeImpl(@Nonnull Supplier<T> supplier) {
        this.supplier = supplier;
        obj = null;
    }

    @Nonnull
    @Override
    public Supplier<T> getSupplier() {
        return supplier;
    }

    @Nonnull
    @Override
    public T get() {
        T obj = this.obj;
        if (obj == null) {
            synchronized (syncRoot) {
                UnsafeHelper.getUnsafe().fullFence();
                obj = this.obj;
                if (obj == null) {
                    this.obj = obj = Objects.requireNonNull(supplier.get());
                }
            }
        }
        return obj;
    }

    @Nullable
    @Override
    public T getUnsafe() {
        return obj;
    }
}
