package org.ricetea.utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public interface Box<T> extends Property<T> {

    @Nonnull
    static <T> Box<T> box(@Nullable T obj) {
        return new BoxImpl<>(obj);
    }

    @Nonnull
    static <T> Box<T> boxThreadSafe(@Nullable T obj) {
        return new BoxThreadSafeImpl<>(obj);
    }

    @Nullable
    T unbox();

    @Nullable
    T exchange(@Nullable T obj);

    @Override
    @Nullable
    default T get() {
        return unbox();
    }

    @Override
    @Nullable
    default T set(@Nullable T obj) {
        exchange(obj);
        return obj;
    }

    default boolean contains(@Nullable T obj) {
        return Objects.equals(unbox(), obj);
    }

    default boolean isNull() {
        return unbox() == null;
    }

    default boolean isNotNull() {
        return unbox() != null;
    }

    @Override
    @Nonnull
    default PropertyType getPropertyType() {
        return PropertyType.ReadWrite;
    }
}
