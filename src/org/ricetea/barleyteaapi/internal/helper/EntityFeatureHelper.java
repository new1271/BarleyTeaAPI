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
        EntityRegister register = EntityRegister.getInstanceUnsafe();
        if (entity != null && event != null && register != null) {
            NamespacedKey id = BaseEntity.getEntityID(entity);
            if (id != null) {
                TFeature feature = ObjectUtil.tryCast(register.lookup(id), featureClass);
                if (feature != null) {
                    try {
                        boolean result = featureFunc.test(feature, dataConstructor.apply(event));
                        if (event instanceof Cancellable cancellable) {
                            result &= !cancellable.isCancelled();
                        }
                        return result;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
        EntityRegister register = EntityRegister.getInstanceUnsafe();
        if (entity != null && event != null && register != null) {
            NamespacedKey id = BaseEntity.getEntityID(entity);
            if (id != null) {
                TFeature feature = ObjectUtil.tryCast(register.lookup(id), featureClass);
                if (feature != null) {
                    try {
                        boolean result = featureFunc.test(feature, dataConstructor.apply(event, event2));
                        if (event instanceof Cancellable cancellable) {
                            result &= !cancellable.isCancelled();
                        }
                        return result;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
        EntityRegister register = EntityRegister.getInstanceUnsafe();
        if (entity != null && event != null && register != null) {
            NamespacedKey id = BaseEntity.getEntityID(entity);
            if (id != null) {
                try {
                    TFeature feature = ObjectUtil.tryCast(register.lookup(id), featureClass);
                    if (feature != null) {
                        featureFunc.accept(feature, dataConstructor.apply(event, event2));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static <TEvent extends Event, TData extends BaseFeatureData<TEvent>, TFeature> void doFeature(
            @Nullable Entity entity, @Nullable TEvent event, @Nonnull Class<TFeature> featureClass,
            @Nonnull BiConsumer<TFeature, TData> featureFunc,
            @Nonnull Function<TEvent, TData> dataConstructor) {
        EntityRegister register = EntityRegister.getInstanceUnsafe();
        if (entity != null && event != null && register != null) {
            NamespacedKey id = BaseEntity.getEntityID(entity);
            if (id != null) {
                try {
                    TFeature feature = ObjectUtil.tryCast(register.lookup(id), featureClass);
                    if (feature != null) {
                        featureFunc.accept(feature, dataConstructor.apply(event));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static <TFeature> void doFeature(
            @Nullable Entity entity, @Nonnull Class<TFeature> featureClass,
            @Nonnull BiConsumer<TFeature, Entity> featureFunc) {
        EntityRegister register = EntityRegister.getInstanceUnsafe();
        if (entity != null && register != null) {
            NamespacedKey id = BaseEntity.getEntityID(entity);
            if (id != null) {
                try {
                    TFeature feature = ObjectUtil.tryCast(register.lookup(id), featureClass);
                    if (feature != null) {
                        featureFunc.accept(feature, entity);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
