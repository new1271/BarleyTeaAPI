package org.ricetea.utils;

import org.ricetea.utils.function.NonnullSupplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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

    @SuppressWarnings("unchecked")
    @Nonnull
    public static <T> List<T> toUnmodifiableList(@Nullable Collection<T> collection) {
        if (collection == null)
            return Collections.emptyList();
        if (collection instanceof List<?> entityList) {
            return (List<T>) Collections.unmodifiableList(entityList);
        } else
            return collection.stream().toList();
    }
}
