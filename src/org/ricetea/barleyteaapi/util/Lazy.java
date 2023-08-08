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

    public T get() {
        if (realObj == null)
            return realObj = supplier.get();
        else
            return realObj;
    }
}
