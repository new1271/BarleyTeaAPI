package org.ricetea.barleyteaapi.util;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface LazyInitializer<T> {

    @Nonnull
    T init();
}
