package org.ricetea.barleyteaapi.internal.base.registration;

import org.ricetea.barleyteaapi.api.base.CustomObject;
import org.ricetea.barleyteaapi.api.base.Feature;
import org.ricetea.barleyteaapi.api.base.registration.CustomObjectRegister;
import org.ricetea.utils.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CustomObjectRegisterBase<T extends CustomObject<F>, F extends Feature> extends NSKeyedRegisterBase<T>
        implements CustomObjectRegister<T, F> {

    @Nonnull
    private final Map<Class<? extends F>, Collection<T>> featureMultiMap = new ConcurrentHashMap<>();

    @Nonnull
    @Override
    public <R extends F> Collection<R> listAllOfFeature(@Nonnull Class<R> clazz, @Nullable Predicate<? super R> predicate) {
        Collection<T> collection = featureMultiMap.get(clazz);
        if (collection == null)
            return Collections.emptySet();
        Stream<T> stream = collection.stream();
        if (getCachedSize() >= Constants.MIN_ITERATION_COUNT_FOR_PARALLEL) {
            stream = stream.parallel();
        }
        var stream2 = stream
                .filter(Objects::nonNull)
                .map(val -> val.getFeature(clazz));
        if (predicate != null) {
            stream2 = stream2.filter(predicate);
        }
        return stream2.collect(Collectors.toUnmodifiableSet());
    }

    @Nullable
    @Override
    public <R extends F> R findFirstOfFeature(@Nonnull Class<R> clazz, @Nullable Predicate<? super R> predicate) {
        Collection<T> collection = featureMultiMap.get(clazz);
        if (collection == null)
            return null;
        Stream<R> stream = collection
                .stream()
                .filter(Objects::nonNull)
                .map(val -> val.getFeature(clazz));
        if (predicate != null) {
            stream = stream.filter(predicate);
        }
        return stream.findFirst().orElse(null);
    }

    @Override
    public <R extends F> boolean hasFeature(@Nonnull Class<R> clazz) {
        return featureMultiMap.containsKey(clazz);
    }

    protected void registerFeatures(@Nonnull T object) {
        for (Class<? extends F> clazz : object.getFeatures()) {
            featureMultiMap.compute(clazz, (val, obj) -> {
                if (obj == null)
                    obj = new HashSet<>(1);
                obj.add(object);
                return obj;
            });
        }
    }

    protected void unregisterFeatures(@Nonnull T object) {
        for (Class<? extends F> clazz : object.getFeatures()) {
            featureMultiMap.computeIfPresent(clazz, (val, obj) -> {
                obj.remove(object);
                return obj.isEmpty() ? null : obj;
            });
        }
    }
}
