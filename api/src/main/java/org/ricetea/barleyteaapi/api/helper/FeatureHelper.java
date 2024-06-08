package org.ricetea.barleyteaapi.api.helper;

import org.ricetea.barleyteaapi.api.base.CustomObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class FeatureHelper {
    @Nullable
    public static <T> T getFeatureUnsafe(@Nullable CustomObject<? super T> obj, @Nullable Class<T> featureClazz) {
        if (obj == null || featureClazz == null)
            return null;
        return obj.getFeature(featureClazz);
    }

    @Nonnull
    public static <T> T getFeature(@Nonnull CustomObject<? super T> obj, @Nonnull Class<T> featureClazz) {
        return Objects.requireNonNull(obj.getFeature(featureClazz));
    }

    public static <T> boolean hasFeature(@Nullable CustomObject<? super T> obj, @Nullable Class<T> featureClazz) {
        if (obj == null || featureClazz == null)
            return false;
        return obj.hasFeature(featureClazz);
    }

    public static <T> void callIfHasFeature(
            @Nullable CustomObject<? super T> obj,
            @Nullable Class<T> featureClazz,
            @Nullable Consumer<T> consumer) {
        if (consumer == null)
            return;
        T feature = getFeatureUnsafe(obj, featureClazz);
        if (feature == null)
            return;
        consumer.accept(feature);
    }

    @Nullable
    public static <T, R> R mapIfHasFeature(
            @Nullable CustomObject<? super T> obj,
            @Nullable Class<T> featureClazz,
            @Nullable Function<T, R> function) {
        if (function == null)
            return null;
        T feature = getFeatureUnsafe(obj, featureClazz);
        if (feature == null)
            return null;
        return function.apply(feature);
    }
}
