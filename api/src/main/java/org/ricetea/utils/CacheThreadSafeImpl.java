package org.ricetea.utils;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

class CacheThreadSafeImpl<T> extends LazyThreadSafeImpl<T> implements Cache<T> {

    CacheThreadSafeImpl(@Nonnull Supplier<T> supplier) {
        super(supplier);
    }

    @Override
    public void reset() {
        obj = null;
    }
}
