package org.ricetea.barleyteaapi.internal.helper;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.ricetea.barleyteaapi.api.abstracts.BaseFeatureData;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.barleyteaapi.util.ObjectUtil;

public final class EntityFeatureHelper {
    public static <TEvent extends Event, TData extends BaseFeatureData<TEvent>, TFeature> boolean doFeatureCancellable(
            @Nullable Entity entity, @Nullable TEvent event, @Nonnull Class<TFeature> featureClass,
            @Nonnull BiPredicate<TFeature, TData> featureFunc,
            @Nonnull Function<TEvent, TData> dataConstructor) {
        if (entity != null && event != null) {
            NamespacedKey id = BaseEntity.getEntityID(entity);
            if (id != null) {
                TFeature feature = ObjectUtil.tryCast(EntityRegister.getInstance().lookupEntityType(id), featureClass);
                if (feature != null) {
                    return featureFunc.test(feature, dataConstructor.apply(event)) && !ObjectUtil.letNonNull(
                            ObjectUtil.mapWhenNonnull(ObjectUtil.tryCast(event, Cancellable.class),
                                    Cancellable::isCancelled),
                            false);
                }
            }
        }
        return true;
    }

    public static <TEvent extends Event, TEvent2 extends Event, TData extends BaseFeatureData<TEvent>, TFeature> boolean doFeatureCancellable(
            @Nullable Entity entity, @Nullable TEvent event, @Nullable TEvent2 event2,
            @Nonnull Class<TFeature> featureClass,
            @Nonnull BiPredicate<TFeature, TData> featureFunc,
            @Nonnull BiFunction<TEvent, TEvent2, TData> dataConstructor) {
        if (entity != null && event != null) {
            NamespacedKey id = BaseEntity.getEntityID(entity);
            if (id != null) {
                TFeature feature = ObjectUtil.tryCast(EntityRegister.getInstance().lookupEntityType(id), featureClass);
                if (feature != null) {
                    return featureFunc.test(feature, dataConstructor.apply(event, event2)) && !ObjectUtil.letNonNull(
                            ObjectUtil.mapWhenNonnull(ObjectUtil.tryCast(event, Cancellable.class),
                                    Cancellable::isCancelled),
                            false);
                }
            }
        }
        return true;
    }

    public static <TEvent extends Event, TEvent2 extends Event, TData extends BaseFeatureData<TEvent>, TFeature> void doFeature(
            @Nullable Entity entity, @Nullable TEvent event, @Nullable TEvent2 event2,
            @Nonnull Class<TFeature> featureClass,
            @Nonnull BiConsumer<TFeature, TData> featureFunc,
            @Nonnull BiFunction<TEvent, TEvent2, TData> dataConstructor) {
        if (entity != null && event != null) {
            NamespacedKey id = BaseEntity.getEntityID(entity);
            if (id != null) {
                TFeature feature = ObjectUtil.tryCast(EntityRegister.getInstance().lookupEntityType(id), featureClass);
                if (feature != null) {
                    featureFunc.accept(feature, dataConstructor.apply(event, event2));
                }
            }
        }
    }

    public static <TEvent extends Event, TData extends BaseFeatureData<TEvent>, TFeature> void doFeature(
            @Nullable Entity entity, @Nullable TEvent event, @Nonnull Class<TFeature> featureClass,
            @Nonnull BiConsumer<TFeature, TData> featureFunc,
            @Nonnull Function<TEvent, TData> dataConstructor) {
        if (entity != null && event != null) {
            NamespacedKey id = BaseEntity.getEntityID(entity);
            if (id != null) {
                TFeature feature = ObjectUtil.tryCast(EntityRegister.getInstance().lookupEntityType(id), featureClass);
                if (feature != null) {
                    featureFunc.accept(feature, dataConstructor.apply(event));
                }
            }
        }
    }

    public static <TFeature> void doFeature(
            @Nullable Entity entity, @Nonnull Class<TFeature> featureClass,
            @Nonnull BiConsumer<TFeature, Entity> featureFunc) {
        if (entity != null) {
            NamespacedKey id = BaseEntity.getEntityID(entity);
            if (id != null) {
                TFeature feature = ObjectUtil.tryCast(EntityRegister.getInstance().lookupEntityType(id), featureClass);
                if (feature != null) {
                    featureFunc.accept(feature, entity);
                }
            }
        }
    }
}
