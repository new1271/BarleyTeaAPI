package org.ricetea.barleyteaapi.internal.entity.registration;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.entity.CustomEntity;
import org.ricetea.barleyteaapi.api.entity.feature.*;
import org.ricetea.barleyteaapi.api.entity.helper.EntityHelper;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.barleyteaapi.api.event.EntitiesRegisteredEvent;
import org.ricetea.barleyteaapi.api.event.EntitiesUnregisteredEvent;
import org.ricetea.barleyteaapi.api.localization.LocalizationRegister;
import org.ricetea.barleyteaapi.api.localization.LocalizedMessageFormat;
import org.ricetea.barleyteaapi.internal.base.registration.CustomObjectRegisterBase;
import org.ricetea.barleyteaapi.internal.linker.EntityFeatureLinker;
import org.ricetea.barleyteaapi.internal.task.EntityTickTask;
import org.ricetea.barleyteaapi.util.SyncUtil;
import org.ricetea.utils.Constants;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
@ApiStatus.Internal
public final class EntityRegisterImpl extends CustomObjectRegisterBase<CustomEntity, EntityFeature> implements EntityRegister {

    @Override
    public void register(@Nullable CustomEntity entity) {
        if (entity == null)
            return;
        registerAll(Set.of(entity));
    }

    @Override
    public void registerAll(@Nullable Collection<CustomEntity> entities) {
        if (entities == null)
            return;
        LocalizationRegister localizationRegister = LocalizationRegister.getInstance();
        refreshCustomEntities(
                entities.stream()
                        .map(_entity -> RefreshCustomEntityRecord.create(getLookupMap().put(_entity.getKey(), _entity),
                                _entity))
                        .filter(Objects::nonNull)
                        .toList());
        entities.forEach(_entity -> {
            if (_entity == null)
                return;
            String translationKey = _entity.getTranslationKey();
            LocalizedMessageFormat oldFormat = localizationRegister.lookup(translationKey);
            if (oldFormat != null && oldFormat.getLocales().contains(LocalizedMessageFormat.DEFAULT_LOCALE))
                return;
            LocalizedMessageFormat format = LocalizedMessageFormat.create(translationKey);
            if (oldFormat != null) {
                oldFormat.getLocales().forEach(locale ->
                        format.setFormat(locale, oldFormat.getFormat(locale)));
            }
            format.setFormat(new MessageFormat(_entity.getDefaultName()));
            localizationRegister.register(format);
            registerFeatures(_entity);
        });
        BarleyTeaAPI inst = BarleyTeaAPI.getInstanceUnsafe();
        if (inst != null) {
            Logger logger = inst.getLogger();
            entities.forEach(_entity -> {
                if (_entity == null)
                    return;
                logger.info(LOGGING_REGISTERED_FORMAT.formatted(_entity.getKey(), "entity"));
                checkFeature(logger, _entity);
            });
        }
        SyncUtil.callInMainThread(inst,
                () -> Bukkit.getPluginManager().callEvent(new EntitiesRegisteredEvent(entities)),
                false);
    }

    private void checkFeature(@Nullable Logger logger, @Nonnull CustomEntity entity) {
        if (logger == null)
            return;
        Class<? extends Entity> originalClazz = entity.getOriginalType().getEntityClass();
        if (originalClazz == null)
            return;
        if (entity instanceof FeatureNaturalSpawn &&
                !(Mob.class.isAssignableFrom(originalClazz) || Creature.class.isAssignableFrom(originalClazz)))
            logger.warning(entity.getKey()
                    + " isn't based on an entity that can be spawned naturally, so FeatureNaturalSpawn won't triggered!");
        if (entity instanceof FeatureSlimeSplit &&
                !Slime.class.isAssignableFrom(originalClazz))
            logger.warning(entity.getKey()
                    + " isn't based on a slime-type mob, so FeatureSlimeSplit won't triggered!");
        if (entity instanceof FeatureProjectile &&
                !Projectile.class.isAssignableFrom(originalClazz))
            logger.warning(entity.getKey()
                    + " isn't based on a projectile entity, so FeatureProjectile won't triggered!");
    }

    @Override
    public void unregister(@Nullable CustomEntity entity) {
        if (entity == null || !getLookupMap().remove(entity.getKey(), entity))
            return;
        Set<CustomEntity> entities = Set.of(entity);
        refreshCustomEntities(entities.stream()
                .map(_entity -> RefreshCustomEntityRecord.create(_entity, null))
                .toList());
        unregisterFeatures(entity);
        BarleyTeaAPI inst = BarleyTeaAPI.getInstanceUnsafe();
        if (inst != null) {
            Logger logger = inst.getLogger();
            entities.forEach(item ->
                    logger.info(LOGGING_UNREGISTERED_FORMAT.formatted(item.getKey())));
        }
        SyncUtil.callInMainThread(inst,
                () -> Bukkit.getPluginManager().callEvent(new EntitiesUnregisteredEvent(entities)),
                false);
    }

    @Override
    public void unregisterAll(@Nullable Predicate<CustomEntity> predicate) {
        if (isEmpty())
            return;
        Map<NamespacedKey, CustomEntity> lookupMap = getLookupMap();
        Collection<CustomEntity> values = lookupMap.values();
        Stream<CustomEntity> stream = values.stream();
        if (predicate != null) {
            if (getCachedSize() >= Constants.MIN_ITERATION_COUNT_FOR_PARALLEL)
                stream = stream.parallel();
            stream = stream.filter(predicate);
        }
        Set<CustomEntity> entities = stream.collect(Collectors.toUnmodifiableSet());
        if (entities.isEmpty())
            return;
        values.removeAll(entities);
        entities.forEach(this::unregisterFeatures);
        refreshCustomEntities(entities.stream()
                .map(_entity -> RefreshCustomEntityRecord.create(_entity, null))
                .toList());
        BarleyTeaAPI inst = BarleyTeaAPI.getInstanceUnsafe();
        if (inst != null) {
            Logger logger = inst.getLogger();
            entities.forEach(item ->
                    logger.info(LOGGING_UNREGISTERED_FORMAT.formatted(item.getKey())));
        }
        SyncUtil.callInMainThread(inst,
                () -> Bukkit.getPluginManager().callEvent(new EntitiesUnregisteredEvent(entities)),
                false);
    }

    private void refreshCustomEntities(@Nonnull Collection<RefreshCustomEntityRecord> records) {
        if (records.stream().anyMatch(RefreshCustomEntityRecord::needOperate)) {
            for (World world : Bukkit.getWorlds()) {
                for (Entity entity : world.getEntities()) {
                    NamespacedKey key = EntityHelper.getEntityID(entity);
                    if (key == null)
                        continue;
                    records.stream()
                            .filter(record -> key.equals(record.key()))
                            .findAny()
                            .ifPresent(record -> {
                                BarleyTeaAPI plugin = BarleyTeaAPI.getInstanceUnsafe();
                                if (plugin != null) {
                                    BukkitScheduler scheduler = Bukkit.getScheduler();
                                    FeatureEntityLoad feature = record.oldFeature();
                                    if (feature != null) {
                                        final FeatureEntityLoad finalFeature = feature;
                                        scheduler.scheduleSyncDelayedTask(plugin,
                                                () -> EntityFeatureLinker.unloadEntity(finalFeature, entity));
                                    }
                                    feature = record.newFeature();
                                    if (feature != null) {
                                        final FeatureEntityLoad finalFeature = feature;
                                        scheduler.scheduleSyncDelayedTask(plugin,
                                                () -> EntityFeatureLinker.loadEntity(finalFeature, entity));
                                    }
                                }
                                boolean hasTickingOld = record.hasTickingOld();
                                boolean hasTickingNew = record.hasTickingNew();
                                if (hasTickingOld != hasTickingNew) {
                                    if (hasTickingOld) {
                                        EntityTickTask task = EntityTickTask.getInstanceUnsafe();
                                        if (task != null) {
                                            task.removeEntity(entity);
                                        }
                                    } else {
                                        EntityTickTask.getInstance().addEntity(entity);
                                    }
                                }
                            });
                }
            }
        }
        refreshCachedSize();
    }

    private record RefreshCustomEntityRecord(@Nullable NamespacedKey key,
                                             @Nullable FeatureEntityLoad oldFeature,
                                             @Nullable FeatureEntityLoad newFeature,
                                             boolean hasTickingOld, boolean hasTickingNew) {

        @Nullable
        public static RefreshCustomEntityRecord create(@Nullable CustomEntity oldEntity, @Nullable CustomEntity newEntity) {
            CustomEntity compareBlock = newEntity == null ? oldEntity : newEntity;
            if (compareBlock == null)
                return null;
            return new RefreshCustomEntityRecord(compareBlock.getKey(),
                    ObjectUtil.tryCast(oldEntity, FeatureEntityLoad.class),
                    ObjectUtil.tryCast(newEntity, FeatureEntityLoad.class),
                    oldEntity instanceof FeatureEntityTick,
                    newEntity instanceof FeatureEntityTick);
        }

        public boolean needOperate() {
            return hasTickingOld || hasTickingNew || oldFeature != null || newFeature != null;
        }
    }
}
