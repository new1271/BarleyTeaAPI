package org.ricetea.barleyteaapi.util;

import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class Lazy<T> {
    boolean isInited;

    @Nullable
    T realObj;

    @Nonnull
    Supplier<T> supplier;

    public Lazy(@Nonnull Supplier<T> supplier) {
        isInited = false;
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
