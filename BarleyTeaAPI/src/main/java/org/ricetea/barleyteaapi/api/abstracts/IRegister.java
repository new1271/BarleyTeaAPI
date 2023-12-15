package org.ricetea.barleyteaapi.api.abstracts;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;

public interface IRegister<T extends Keyed> {
    void register(@Nullable T key);

    default void registerAll(@Nullable Collection<T> keys) {
        if (keys != null) {
            keys.forEach(this::register);
        }
    }

    void unregister(@Nullable T key);

    void unregisterAll();

    void unregisterAll(@Nullable Predicate<T> predicate);

    @Nullable
    T lookup(@Nullable NamespacedKey key);

    boolean has(@Nullable NamespacedKey key);

    boolean hasAnyRegistered();

    @Nonnull
    Collection<T> listAll();

    @Nonnull
    Collection<T> listAll(@Nullable Predicate<T> predicate);

    @Nonnull
    Collection<NamespacedKey> listAllKeys();

    @Nonnull
    Collection<NamespacedKey> listAllKeys(@Nullable Predicate<T> predicate);

    @Nullable
    T findFirst(@Nullable Predicate<T> predicate);

    @Nullable
    NamespacedKey findFirstKey(@Nullable Predicate<T> predicate);

    public static class Filter<T extends Keyed> implements Predicate<Map.Entry<NamespacedKey, T>> {

        @Nonnull
        Predicate<T> filter;

        public Filter(@Nonnull Predicate<T> filter) {
            this.filter = filter;
        }

        @Override
        public boolean test(Map.Entry<NamespacedKey, T> t) {
            return filter.test(t.getValue());
        }

    }

    public static class Mapper<T extends Keyed> implements Function<Map.Entry<NamespacedKey, T>, NamespacedKey> {

        @Override
        public NamespacedKey apply(Map.Entry<NamespacedKey, T> t) {
            return t.getKey();
        }

    }
}
