package org.ricetea.barleyteaapi.api.base.registration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Predicate;

public interface StringKeyedRegister<T> extends IRegister<T> {

    default void unregister(@Nullable String key) {
        if (key == null)
            return;
        T item = lookup(key);
        if (item == null)
            return;
        unregister(item);
    }

    @Nullable
    T lookup(@Nullable String key);

    default boolean hasRegistered(@Nullable String key) {
        return lookup(key) != null;
    }

    @Nonnull
    default Collection<String> listAllKeys() {
        return listAllKeys(null);
    }

    @Nonnull
    Collection<String> listAllKeys(@Nullable Predicate<? super String> predicate);

    @Nullable
    default String findFirstKey() {
        return findFirstKey(null);
    }

    @Nullable
    String findFirstKey(@Nullable Predicate<? super String> predicate);
}
