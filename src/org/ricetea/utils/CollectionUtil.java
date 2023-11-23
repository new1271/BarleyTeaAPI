package org.ricetea.utils;

import java.util.Collection;
import java.util.Iterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.ricetea.utils.function.NonnullSupplier;

public class CollectionUtil {
    private CollectionUtil() {
    }

    @Nullable
    public static <T> T first(@Nullable Collection<T> collection) {
        if (collection == null || collection.isEmpty())
            return null;
        return collection.iterator().next();
    }

    @Nonnull
    public static <T> T firstOrDefault(@Nullable Collection<T> collection, @Nonnull T defaultValue) {
        return ObjectUtil.letNonNull(first(collection), defaultValue);
    }

    @Nonnull
    public static <T> T firstOrDefault(@Nullable Collection<T> collection, @Nonnull NonnullSupplier<T> supplier) {
        return ObjectUtil.letNonNull(first(collection), supplier);
    }

    @Nullable
    public static <T> T last(@Nullable Collection<T> collection) {
        if (collection == null || collection.isEmpty())
            return null;
        T result = null;
        Iterator<T> iterator = collection.iterator();
        do {
            T newResult = iterator.next();
            if (newResult == null)
                break;
            else
                result = newResult;
        } while (true);
        return result;
    }

    @Nonnull
    public static <T> T lastOrDefault(@Nullable Collection<T> collection, @Nonnull T defaultValue) {
        return ObjectUtil.letNonNull(last(collection), defaultValue);
    }

    @Nonnull
    public static <T> T lastOrDefault(@Nullable Collection<T> collection, @Nonnull NonnullSupplier<T> supplier) {
        return ObjectUtil.letNonNull(last(collection), supplier);
    }
}
