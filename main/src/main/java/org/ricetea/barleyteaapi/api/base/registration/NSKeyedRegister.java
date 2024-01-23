package org.ricetea.barleyteaapi.api.base.registration;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Predicate;

public interface NSKeyedRegister<T extends Keyed> extends IRegister<T> {

    default void unregister(@Nullable NamespacedKey key) {
        if (key == null)
            return;
        T item = lookup(key);
        if (item == null)
            return;
        unregister(item);
    }

    @Nullable
    T lookup(@Nullable NamespacedKey key);

    default boolean hasRegistered(@Nullable NamespacedKey key) {
        return lookup(key) != null;
    }

    @Override
    default boolean hasRegistered(@Nullable T item) { //Replaced to better implementation
        if (item == null)
            return false;
        return lookup(item.getKey()) == item;
    }

    @Nonnull
    default Collection<NamespacedKey> listAllKeys() {
        return listAllKeys(null);
    }

    @Nonnull
    Collection<NamespacedKey> listAllKeys(@Nullable Predicate<NamespacedKey> predicate);

    @Nullable
    default NamespacedKey findFirstKey() {
        return findFirstKey(null);
    }

    @Nullable
    NamespacedKey findFirstKey(@Nullable Predicate<NamespacedKey> predicate);
}
