package org.ricetea.barleyteaapi.internal.linker;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.base.data.BaseFeatureData;
import org.ricetea.barleyteaapi.api.entity.CustomEntity;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityLoad;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

@ApiStatus.Internal
public final class EntityFeatureLinker {

    @Nonnull
    private static final Set<Entity> loadedEntitys = Collections.newSetFromMap(new WeakHashMap<>());

    public static <TEvent extends Event, TData extends BaseFeatureData<TEvent>, TFeature> boolean doFeatureCancellable(
            @Nullable Entity entity, @Nullable TEvent event, @Nonnull Class<TFeature> featureClass,
            @Nonnull BiPredicate<TFeature, TData> featureFunc,
            @Nonnull Function<TEvent, TData> dataConstructor) {
        if (entity == null || event == null || !EntityRegister.hasRegistered())
            return true;
        TFeature feature = ObjectUtil.tryCast(CustomEntity.get(entity), featureClass);
        if (feature == null)
            return true;
        return ObjectUtil.tryMap(() -> {
            boolean result = featureFunc.test(feature, dataConstructor.apply(event));
            if (event instanceof Cancellable cancellable) {
                result &= !cancellable.isCancelled();
            }
            return result;
        }, true);
    }

    public static <TEvent extends Event, TEvent2 extends Event, TData extends BaseFeatureData<TEvent>, TFeature> boolean doFeatureCancellable(
            @Nullable Entity entity, @Nullable TEvent event, @Nullable TEvent2 event2,
            @Nonnull Class<TFeature> featureClass,
            @Nonnull BiPredicate<TFeature, TData> featureFunc,
            @Nonnull BiFunction<TEvent, TEvent2, TData> dataConstructor) {
        if (entity == null || event == null || !EntityRegister.hasRegistered())
            return true;
        TFeature feature = ObjectUtil.tryCast(CustomEntity.get(entity), featureClass);
        if (feature == null)
            return true;
        return ObjectUtil.tryMap(() -> {
            boolean result = featureFunc.test(feature, dataConstructor.apply(event, event2));
            if (event instanceof Cancellable cancellable) {
                result &= !cancellable.isCancelled();
            }
            return result;
        }, true);
    }

    public static <TEvent extends Event, TEvent2 extends Event, TData extends BaseFeatureData<TEvent>, TFeature> void doFeature(
            @Nullable Entity entity, @Nullable TEvent event, @Nullable TEvent2 event2,
            @Nonnull Class<TFeature> featureClass,
            @Nonnull BiConsumer<TFeature, TData> featureFunc,
            @Nonnull BiFunction<TEvent, TEvent2, TData> dataConstructor) {
        if (entity == null || event == null || !EntityRegister.hasRegistered())
            return;
        TFeature feature = ObjectUtil.tryCast(CustomEntity.get(entity), featureClass);
        if (feature == null)
            return;
        ObjectUtil.tryCall(() -> featureFunc.accept(feature, dataConstructor.apply(event, event2)));
    }

    public static <TEvent extends Event, TData extends BaseFeatureData<TEvent>, TFeature> void doFeature(
            @Nullable Entity entity, @Nullable TEvent event, @Nonnull Class<TFeature> featureClass,
            @Nonnull BiConsumer<TFeature, TData> featureFunc,
            @Nonnull Function<TEvent, TData> dataConstructor) {
        if (entity == null || event == null || !EntityRegister.hasRegistered())
            return;
        TFeature feature = ObjectUtil.tryCast(CustomEntity.get(entity), featureClass);
        if (feature == null)
            return;
        ObjectUtil.tryCall(() -> featureFunc.accept(feature, dataConstructor.apply(event)));
    }

    public static <TFeature> void doFeature(
            @Nullable Entity entity, @Nonnull Class<TFeature> featureClass,
            @Nonnull BiConsumer<TFeature, Entity> featureFunc) {
        if (entity == null || !EntityRegister.hasRegistered())
            return;
        TFeature feature = ObjectUtil.tryCast(CustomEntity.get(entity), featureClass);
        if (feature == null)
            return;
        ObjectUtil.tryCall(() -> featureFunc.accept(feature, entity));
    }

    public static void loadEntity(@Nonnull Entity entity) {
        CustomEntity entityType = CustomEntity.get(entity);
        if (entityType == null)
            return;
        loadEntity(entity);
    }

    public static void loadEntity(@Nonnull CustomEntity entityType, @Nonnull Entity entity) {
        if (entityType instanceof FeatureEntityLoad feature) {
            loadEntity(feature, entity);
        }
    }

    public static void loadEntity(@Nonnull FeatureEntityLoad feature, @Nonnull Entity entity) {
        if (entity.isDead())
            return;
        synchronized (loadedEntitys) {
            if (!loadedEntitys.add(entity))
                return;
        }
        ObjectUtil.tryCall(() -> feature.handleEntityLoaded(entity));
    }

    public static void unloadEntity(@Nonnull Entity entity) {
        CustomEntity entityType = CustomEntity.get(entity);
        if (entityType == null)
            return;
        unloadEntity(entityType, entity);
    }

    public static void unloadEntity(@Nonnull CustomEntity entityType, @Nonnull Entity entity) {
        if (entityType instanceof FeatureEntityLoad feature) {
            unloadEntity(feature, entity);
        }
    }

    public static void unloadEntity(@Nonnull FeatureEntityLoad feature, @Nonnull Entity entity) {
        synchronized (loadedEntitys) {
            if (!loadedEntitys.remove(entity))
                return;
        }
        ObjectUtil.tryCall(() -> feature.handleEntityUnloaded(entity));
    }
}
