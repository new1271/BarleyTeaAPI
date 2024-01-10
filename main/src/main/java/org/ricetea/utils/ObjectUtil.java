package org.ricetea.utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class ObjectUtil {

    @Nonnull
    public static <T> T letNonNull(@Nullable T obj, @Nonnull Supplier<T> supplierWhenNull) {
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
                return (T) obj;
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
    public static <T, R> R safeMap(@Nullable T obj, @Nullable Function<T, R> mapFunction) {
        if (obj == null || mapFunction == null) {
            return null;
        } else {
            return mapFunction.apply(obj);
        }
    }

    public static <T> void safeCall(@Nullable T obj, @Nullable Consumer<T> callFunction) {
        if (obj != null && callFunction != null) {
            callFunction.accept(obj);
        }
    }

    @Nullable
    public static <R> R tryMap(@Nonnull Supplier<R> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nonnull
    public static <R> R tryMap(@Nonnull Supplier<R> supplier, @Nonnull R defaultValue) {
        try {
            return ObjectUtil.letNonNull(supplier.get(), defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    @Nullable
    public static <R> R tryMapSilently(@Nonnull Supplier<R> supplier) {
        try {
            return supplier.get();
        } catch (Exception ignored) {
            return null;
        }
    }

    @Nonnull
    public static <R> R tryMapSilently(@Nonnull Supplier<R> supplier, @Nonnull R defaultValue) {
        try {
            return ObjectUtil.letNonNull(supplier.get(), defaultValue);
        } catch (Exception ignored) {
            return defaultValue;
        }
    }

    public static void tryCall(@Nonnull Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void tryCallSilently(@Nonnull Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception ignored) {
        }
    }
}
