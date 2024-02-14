package org.ricetea.barleyteaapi.internal.base.registration;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.ricetea.barleyteaapi.api.base.CustomObject;
import org.ricetea.barleyteaapi.api.base.Feature;
import org.ricetea.barleyteaapi.api.base.registration.CustomObjectRegister;
import org.ricetea.utils.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CustomObjectRegisterBase<T extends CustomObject<F>, F extends Feature> extends NSKeyedRegisterBase<T>
        implements CustomObjectRegister<T, F> {

    @Nonnull
    private final Multimap<Class<? extends F>, T> featureMultiMap = HashMultimap.create();

    @Nonnull
    @Override
    public <R extends F> Collection<R> listAllOfFeature(@Nonnull Class<R> clazz, @Nullable Predicate<R> predicate) {
        var stream = featureMultiMap.get(clazz).stream();
        if (getCachedSize() >= Constants.MIN_ITERATION_COUNT_FOR_PARALLEL) {
            stream = stream.parallel();
        }
        var stream2 = stream.map(clazz::cast);
        if (predicate != null) {
            stream2 = stream2.filter(predicate);
        }
        return stream2.collect(Collectors.toUnmodifiableSet());
    }

    @Nullable
    @Override
    public <R extends F> R findFirstOfFeature(@Nonnull Class<R> clazz, @Nullable Predicate<R> predicate) {
        var stream = featureMultiMap.get(clazz)
                .stream()
                .map(clazz::cast);
        if (predicate != null) {
            stream = stream.filter(predicate);
        }
        return stream.findFirst().orElse(null);
    }

    protected void registerFeatures(@Nonnull T object) {
        for (Class<? extends F> clazz : object.getFeatures()) {
            featureMultiMap.put(clazz, object);
        }
    }

    protected void unregisterFeatures(@Nonnull T object) {
        for (Class<? extends F> clazz : object.getFeatures()) {
            featureMultiMap.put(clazz, object);
        }
    }
}
