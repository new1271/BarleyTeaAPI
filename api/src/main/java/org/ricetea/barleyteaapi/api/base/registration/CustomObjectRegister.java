package org.ricetea.barleyteaapi.api.base.registration;

import org.ricetea.barleyteaapi.api.base.CustomObject;
import org.ricetea.barleyteaapi.api.base.Feature;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Predicate;

public interface CustomObjectRegister<T extends CustomObject<F>, F extends Feature> extends NSKeyedRegister<T> {

    @Nonnull
    default <R extends F> Collection<R> listAllOfFeature(@Nonnull Class<R> clazz) {
        return listAllOfFeature(clazz, null);
    }

    @Nonnull
    <R extends F> Collection<R> listAllOfFeature(@Nonnull Class<R> clazz, @Nullable Predicate<? super R> predicate);

    @Nullable
    default <R extends F> R findFirstOfFeature(@Nonnull Class<R> clazz) {
        return findFirstOfFeature(clazz, null);
    }

    @Nullable
    <R extends F> R findFirstOfFeature(@Nonnull Class<R> clazz, @Nullable Predicate<? super R> predicate);

    default <R extends F> boolean hasFeature(@Nonnull Class<R> clazz) {
        return findFirstOfFeature(clazz) != null;
    }
}
