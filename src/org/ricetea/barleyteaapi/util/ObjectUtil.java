package org.ricetea.barleyteaapi.util;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ObjectUtil {

    @Nonnull
    public static <T> T letNonNull(@Nullable T obj, @Nonnull Supplier<T> supplierWhenNull) {
        if (obj == null) {
            T result = supplierWhenNull.get();
            if (result == null)
                throw new NullPointerException();
            return result;
        }
        return obj;
    }

    @Nonnull
    public static <T> T letNonNull(@Nullable T obj, @Nonnull T objWhenNull) {
        return obj == null ? objWhenNull : obj;
    }

    @Nullable
    public static <T> T tryCast(@Nullable Object obj, @Nonnull Class<T> castClass) {
        if (obj == null)
            return null;
        else {
            return castClass.isInstance(obj) ? castClass.cast(obj) : null;
        }
    }

    @Nonnull
    public static <T> T throwWhenNull(@Nullable T obj) throws NullPointerException {
        if (obj == null)
            throw new NullPointerException();
        else
            return obj;
    }

    @Nullable
    public static <T, R> R callWhenNonnull(@Nullable T obj, Function<T, R> callFunction) {
        if (obj == null) {
            return null;
        } else {
            return callFunction.apply(obj);
        }
    }

    public static <T> void callWhenNonnull(@Nullable T obj, Consumer<T> callFunction) {
        if (obj != null) {
            callFunction.accept(obj);
        }
    }
}
