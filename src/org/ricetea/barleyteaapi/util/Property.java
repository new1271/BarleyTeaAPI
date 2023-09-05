package org.ricetea.barleyteaapi.util;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Property<T> extends UnaryOperator<T>, Supplier<T>, Consumer<T> {

    @Nullable
    T get();

    @Nullable
    T set(T obj);

    @Nonnull
    PropertyType getPropertyType();

    default T apply(T obj) {
        return set(obj);
    }

    default void accept(T obj) {
        set(obj);
    }

    public enum PropertyType{
        ReadWrite,
        ReadOnly,
        WriteOnly
    }
}
