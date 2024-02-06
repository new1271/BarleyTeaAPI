package org.ricetea.utils;

import javax.annotation.Nonnull;

public record WithFlag<T>(@Nonnull T obj, boolean flag) {
    public WithFlag(@Nonnull T obj) {
        this(obj, false);
    }
}
