package org.ricetea.utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

class LazyThreadSafeImpl<T> implements Lazy<T> {

    @Nonnull
    protected final AtomicReference<T> objReference;
    @Nonnull
    private final Supplier<T> supplier;

    LazyThreadSafeImpl(@Nonnull Supplier<T> supplier) {
        this.supplier = supplier;
        objReference = new AtomicReference<>(null);
    }

    @Nonnull
    @Override
    public Supplier<T> getSupplier() {
        return supplier;
    }

    @Nonnull
    @Override
    public T get() {
        return objReference.updateAndGet(this::update);
    }

    @Nullable
    @Override
    public T getUnsafe() {
        return objReference.get();
    }

    private T update(T old) {
        if (old == null)
            return supplier.get();
        return old;
    }
}
