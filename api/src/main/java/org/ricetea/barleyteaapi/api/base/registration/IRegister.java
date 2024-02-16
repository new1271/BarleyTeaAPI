package org.ricetea.barleyteaapi.api.base.registration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Predicate;

public interface IRegister<T> {

    void register(@Nullable T item);

    default void registerAll(@Nullable Collection<? extends T> items) {
        if (items != null) {
            items.forEach(this::register);
        }
    }

    void unregister(@Nullable T item);

    default void unregisterAll() {
        unregisterAll(null);
    }

    void unregisterAll(@Nullable Predicate<? super T> predicate);

    default boolean isEmpty() {
        return findFirst() == null;
    }

    default boolean hasRegistered(@Nullable T item) {
        if (item == null)
            return false;
        return findFirst(Predicate.isEqual(item)) != null;
    }

    @Nonnull
    default Collection<T> listAll() {
        return listAll(null);
    }

    @Nonnull
    Collection<T> listAll(@Nullable Predicate<? super T> predicate);

    @Nullable
    default T findFirst() {
        return findFirst(null);
    }

    @Nullable
    T findFirst(@Nullable Predicate<? super T> predicate);
}
