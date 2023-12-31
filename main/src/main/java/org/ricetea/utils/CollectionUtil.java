package org.ricetea.utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

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
    public static <T> T firstOrDefault(@Nullable Collection<T> collection, @Nonnull Supplier<T> supplier) {
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
    public static <T> T lastOrDefault(@Nullable Collection<T> collection, @Nonnull Supplier<T> supplier) {
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

    public static <K, V, T extends Map.Entry<K, V>> void forEach(
            @Nullable Collection<T> collection, @Nonnull BiConsumer<K, V> forEachConsumer) {
        if (collection == null)
            return;
        for (T entry : collection)
            forEachConsumer.accept(entry.getKey(), entry.getValue());
    }
}
