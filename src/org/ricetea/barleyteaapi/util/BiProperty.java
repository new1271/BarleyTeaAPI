package org.ricetea.barleyteaapi.util;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface BiProperty<T, R> extends BiConsumer<T, R>, BiFunction<T, R, R> {

    @Nullable
    R get(T obj);

    @Nullable
    R set(T obj, R value);

    @Nonnull
    PropertyType getPropertyType();

    default R apply(T obj, R value) {
        return set(obj, value);
    }

    default void accept(T obj, R value) {
        set(obj, value);
    }

    public enum PropertyType{
        ReadWrite,
        ReadOnly,
        WriteOnly
    }
}
