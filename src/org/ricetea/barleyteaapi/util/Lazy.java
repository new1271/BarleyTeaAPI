package org.ricetea.barleyteaapi.util;

import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class Lazy<T> implements Supplier<T> {
    @Nonnull
    protected final Supplier<T> supplier;

    @Nullable
    protected T realObj;

    public Lazy(@Nonnull Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Nonnull
    public T get() {
        T obj = realObj;
        if (obj == null) {
            return realObj = ObjectUtil.throwWhenNull(supplier.get());
        }
        return obj;
    }

    @Nullable
    public T getUnsafe() {
        return realObj;
    }
}
