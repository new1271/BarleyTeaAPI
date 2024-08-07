package org.ricetea.barleyteaapi.internal.linker;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.base.data.BaseFeatureData;
import org.ricetea.barleyteaapi.api.entity.CustomEntity;
import org.ricetea.barleyteaapi.api.entity.feature.EntityFeature;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityLoad;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityMove;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityTick;
import org.ricetea.barleyteaapi.api.entity.feature.monitor.FeatureMonitorEntityMove;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.barleyteaapi.internal.listener.EntityMoveListener;
import org.ricetea.barleyteaapi.internal.listener.monitor.EntityMoveMonitor;
import org.ricetea.barleyteaapi.internal.task.EntityTickTask;
import org.ricetea.barleyteaapi.util.SyncUtil;
import org.ricetea.utils.ChainedRunner;
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

    public static <TEvent extends Event, TData extends BaseFeatureData<TEvent>, TFeature extends EntityFeature> boolean doFeatureCancellable(
            @Nullable Entity entity, @Nullable TEvent event, @Nonnull Class<TFeature> featureClass,
            @Nonnull BiPredicate<TFeature, TData> featureFunc,
            @Nonnull Function<TEvent, TData> dataConstructor) {
        if (entity == null || event == null || !EntityRegister.hasRegistered())
            return true;
        TFeature feature = ObjectUtil.tryMap(CustomEntity.get(entity), obj -> obj.getFeature(featureClass));
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

    public static <TEvent extends Event, TEvent2 extends Event, TData extends BaseFeatureData<TEvent>,
            TFeature extends EntityFeature> boolean doFeatureCancellable(
            @Nullable Entity entity, @Nullable TEvent event, @Nullable TEvent2 event2,
            @Nonnull Class<TFeature> featureClass,
            @Nonnull BiPredicate<TFeature, TData> featureFunc,
            @Nonnull BiFunction<TEvent, TEvent2, TData> dataConstructor) {
        if (entity == null || event == null || !EntityRegister.hasRegistered())
            return true;
        TFeature feature = ObjectUtil.tryMap(CustomEntity.get(entity), obj -> obj.getFeature(featureClass));
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

    public static <TEvent extends Event, TEvent2 extends Event, TData extends BaseFeatureData<TEvent>,
            TFeature extends EntityFeature> void doFeature(
            @Nullable Entity entity, @Nullable TEvent event, @Nullable TEvent2 event2,
            @Nonnull Class<TFeature> featureClass,
            @Nonnull BiConsumer<TFeature, TData> featureFunc,
            @Nonnull BiFunction<TEvent, TEvent2, TData> dataConstructor) {
        if (entity == null || event == null || !EntityRegister.hasRegistered())
            return;
        TFeature feature = ObjectUtil.tryMap(CustomEntity.get(entity), obj -> obj.getFeature(featureClass));
        if (feature == null)
            return;
        ObjectUtil.tryCall(() -> featureFunc.accept(feature, dataConstructor.apply(event, event2)));
    }

    public static <TEvent extends Event, TData extends BaseFeatureData<TEvent>,
            TFeature extends EntityFeature> void doFeature(
            @Nullable Entity entity, @Nullable TEvent event, @Nonnull Class<TFeature> featureClass,
            @Nonnull BiConsumer<TFeature, TData> featureFunc,
            @Nonnull Function<TEvent, TData> dataConstructor) {
        if (entity == null || event == null || !EntityRegister.hasRegistered())
            return;
        TFeature feature = ObjectUtil.tryMap(CustomEntity.get(entity), obj -> obj.getFeature(featureClass));
        if (feature == null)
            return;
        ObjectUtil.tryCall(() -> featureFunc.accept(feature, dataConstructor.apply(event)));
    }

    public static <TFeature extends EntityFeature> void doFeature(
            @Nullable Entity entity, @Nonnull Class<TFeature> featureClass,
            @Nonnull BiConsumer<TFeature, Entity> featureFunc) {
        if (entity == null || !EntityRegister.hasRegistered())
            return;
        TFeature feature = ObjectUtil.tryMap(CustomEntity.get(entity), obj -> obj.getFeature(featureClass));
        if (feature == null)
            return;
        ObjectUtil.tryCall(() -> featureFunc.accept(feature, entity));
    }

    public static void loadEntity(@Nonnull Entity entity, boolean loadOnly) {
        CustomEntity entityType = CustomEntity.get(entity);
        if (entityType == null)
            return;
        loadEntity(entityType, entity, loadOnly);
    }

    public static void loadEntity(@Nonnull CustomEntity entityType, @Nonnull Entity entity, boolean loadOnly) {
        if (entity.isDead())
            return;
        if (entityType.hasFeature(FeatureMonitorEntityMove.class)) {
            EntityMoveMonitor.getInstance().addReference();
        }
        if (entityType.hasFeature(FeatureEntityMove.class)) {
            EntityMoveListener.getInstance().addReference();
        }
        FeatureEntityLoad feature = entityType.getFeature(FeatureEntityLoad.class);
        boolean needTick = !loadOnly && entityType.getFeature(FeatureEntityTick.class) != null;
        if (feature == null && !needTick)
            return;
        synchronized (loadedEntitys) {
            if (!loadedEntitys.add(entity))
                return;
        }
        ObjectUtil.tryCall(feature, _feature ->
                SyncUtil.callInMainThread(() -> _feature.handleEntityLoaded(entity)));
        if (needTick)
            EntityTickTask.getInstance().addEntity(entity);
    }

    public static void unloadEntity(@Nonnull Entity entity) {
        CustomEntity entityType = CustomEntity.get(entity);
        if (entityType == null)
            return;
        unloadEntity(entityType, entity);
    }

    public static void unloadEntity(@Nonnull CustomEntity entityType, @Nonnull Entity entity) {
        synchronized (loadedEntitys) {
            if (!loadedEntitys.remove(entity))
                return;
        }
        if (entityType.hasFeature(FeatureMonitorEntityMove.class)) {
            ObjectUtil.safeCall(EntityMoveMonitor.getInstanceUnsafe(), EntityMoveMonitor::removeReference);
        }
        if (entityType.hasFeature(FeatureEntityMove.class)) {
            ObjectUtil.safeCall(EntityMoveListener.getInstanceUnsafe(), EntityMoveListener::removeReference);
        }
        ObjectUtil.tryCall(entityType.getFeature(FeatureEntityLoad.class), _feature ->
                SyncUtil.callInMainThread(() -> _feature.handleEntityUnloaded(entity)));
        if (entityType.getFeature(FeatureEntityTick.class) != null) {
            ObjectUtil.tryCall(EntityTickTask.getInstanceUnsafe(),
                    task -> task.removeEntity(entity));
        }
    }

    public static void refreshEntity(@Nonnull Entity entity, @Nonnull RefreshCustomEntityRecord record) {
        FeatureEntityLoad oldLoadFeature = record.oldLoadFeature();
        FeatureEntityLoad newLoadFeature = record.newLoadFeature();
        boolean hasTickingOld = record.hasTickingOld();
        boolean hasTickingNew = record.hasTickingNew();
        synchronized (loadedEntitys) {
            if (oldLoadFeature == null) {
                if (newLoadFeature == null) {
                    loadedEntitys.remove(entity);
                } else {
                    if (!loadedEntitys.add(entity))
                        return;
                }
            } else {
                if (newLoadFeature == null) {
                    if (!loadedEntitys.remove(entity))
                        return;
                }
            }
        }
        ChainedRunner runner = ChainedRunner.create();
        if (oldLoadFeature != null)
            runner = runner.attach(() -> oldLoadFeature.handleEntityUnloaded(entity));
        if (newLoadFeature != null)
            runner = runner.attach(() -> newLoadFeature.handleEntityLoaded(entity));
        runner.freeze().run(SyncUtil::callInMainThread);
        if (hasTickingOld) {
            if (!hasTickingNew) {
                ObjectUtil.safeCall(EntityTickTask.getInstanceUnsafe(),
                        task -> task.removeEntity(entity));
            }
        } else {
            if (hasTickingNew) {
                EntityTickTask.getInstance().addEntity(entity);
            }
        }
    }

    public record RefreshCustomEntityRecord(@Nullable NamespacedKey key,
                                            @Nullable FeatureEntityLoad oldLoadFeature,
                                            @Nullable FeatureEntityLoad newLoadFeature,
                                            boolean hasTickingOld, boolean hasTickingNew) {

        @Nullable
        public static RefreshCustomEntityRecord create(@Nullable CustomEntity oldEntity, @Nullable CustomEntity newEntity) {
            CustomEntity compareBlock = newEntity == null ? oldEntity : newEntity;
            if (compareBlock == null)
                return null;
            return new RefreshCustomEntityRecord(compareBlock.getKey(),
                    ObjectUtil.tryMap(oldEntity, _entity -> _entity.getFeature(FeatureEntityLoad.class)),
                    ObjectUtil.tryMap(newEntity, _entity -> _entity.getFeature(FeatureEntityLoad.class)),
                    ObjectUtil.tryMap(oldEntity,
                            _entity -> _entity.getFeature(FeatureEntityTick.class) != null,
                            false
                    ),
                    ObjectUtil.tryMap(newEntity,
                            _entity -> _entity.getFeature(FeatureEntityTick.class) != null,
                            false
                    ));
        }

        public boolean needOperate() {
            return hasTickingOld || hasTickingNew || oldLoadFeature != null || newLoadFeature != null;
        }
    }
}
