package org.ricetea.barleyteaapi.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class Lazy<T> {
    boolean isInited;

    @Nullable
    T realObj;

    @Nonnull
    LazyInitializer<T> supplier;

    public Lazy(@Nonnull LazyInitializer<T> supplier) {
        isInited = false;
        this.supplier = supplier;
    }

    @Nonnull
    public T get() {
        T obj = realObj;
        if (obj == null)
            return realObj = supplier.init();
        else
            return obj;
    }
}
