package org.ricetea.utils;

import javax.annotation.Nonnull;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ObjectWrapper<T> extends Supplier<T> {

    static <T> ObjectWrapper<T> wrap(@Nonnull T obj) {
        return new ObjectWrapperImpl<>(obj);
    }

    static <T> ObjectWrapper<T> copyOnGet(@Nonnull T obj) {
        return new ObjectWrapperCopyOnGetImpl<>(obj);
    }

    static <T> ObjectWrapper<T> copyOnGet(@Nonnull T obj, @Nonnull Function<T, T> cloningFunction) {
        return new ObjectWrapperCopyOnGetImpl<>(obj, cloningFunction);
    }

    @Nonnull
    T get();

    boolean isCalled();
}
