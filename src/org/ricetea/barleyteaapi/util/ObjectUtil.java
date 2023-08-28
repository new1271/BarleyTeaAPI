package org.ricetea.barleyteaapi.util;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ObjectUtil {

    @Nonnull
    public static <T> T letNonNull(@Nullable T obj, @Nonnull Supplier<T> supplierWhenNull) {
        return obj == null ? Objects.requireNonNull(Objects.requireNonNull(supplierWhenNull).get()) : obj;
    }

    @Nonnull
    public static <T> T letNonNull(@Nullable T obj, @Nonnull T objWhenNull) {
        return obj == null ? Objects.requireNonNull(objWhenNull) : obj;
    }

    @Nullable
    public static <T> T tryCast(@Nullable Object obj, @Nonnull Class<T> castClass) {
        if (obj == null)
            return null;
        else {
            return castClass.isInstance(obj) ? castClass.cast(obj) : null;
        }
    }

    @Nullable
    public static <T> T cast(@Nullable Object obj, @Nonnull Class<T> castClass) {
        if (obj == null)
            return null;
        else {
            return castClass.cast(obj);
        }
    }

    @Nullable
    public static <T, R> R mapWhenNonnull(@Nullable T obj, @Nullable Function<T, R> mapFunction) {
        if (obj == null || mapFunction == null) {
            return null;
        } else {
            return mapFunction.apply(obj);
        }
    }

    public static <T> void callWhenNonnull(@Nullable T obj, @Nullable Consumer<T> callFunction) {
        if (obj != null && callFunction != null) {
            callFunction.accept(obj);
        }
    }
}
