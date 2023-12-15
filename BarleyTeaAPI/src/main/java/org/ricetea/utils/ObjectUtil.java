package org.ricetea.utils;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.ricetea.utils.function.NonnullConsumer;
import org.ricetea.utils.function.NonnullFunction;
import org.ricetea.utils.function.NonnullSupplier;

public final class ObjectUtil {

    @Nonnull
    public static <T> T letNonNull(@Nullable T obj, @Nonnull NonnullSupplier<T> supplierWhenNull) {
        return obj == null ? Objects.requireNonNull(Objects.requireNonNull(supplierWhenNull).get()) : obj;
    }

    @Nonnull
    public static <T> T letNonNull(@Nullable T obj, @Nonnull T objWhenNull) {
        return obj == null ? Objects.requireNonNull(objWhenNull) : obj;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> T tryCast(@Nullable Object obj, @Nonnull Class<T> castClass) {
        if (obj == null)
            return null;
        else {
            if (castClass.isInstance(obj))
                return castClass.cast(obj);
            else if (castClass.equals(String.class))
                return (T) obj.toString();
            else
                return null;
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
    public static <T, R> R safeMap(@Nullable T obj, @Nullable NonnullFunction<T, R> mapFunction) {
        if (obj == null || mapFunction == null) {
            return null;
        } else {
            return mapFunction.apply(obj);
        }
    }

    public static <T> void safeCall(@Nullable T obj, @Nullable NonnullConsumer<T> callFunction) {
        if (obj != null && callFunction != null) {
            callFunction.accept(obj);
        }
    }
}
