package org.ricetea.utils;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

class CacheImpl<T> extends LazyImpl<T> implements Cache<T> {

    CacheImpl(@Nonnull Supplier<T> supplier) {
        super(supplier);
    }

    @Override
    public void reset() {
        obj = null;
    }
}
