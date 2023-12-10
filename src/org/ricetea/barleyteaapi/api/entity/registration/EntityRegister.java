package org.ricetea.barleyteaapi.api.entity.registration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Slime;
import org.bukkit.scheduler.BukkitScheduler;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.abstracts.IRegister;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureCommandSummon;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityLoad;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityTick;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureNaturalSpawn;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureProjectile;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureSlimeSplit;
import org.ricetea.barleyteaapi.internal.nms.command.NMSRegularCommand;
import org.ricetea.barleyteaapi.internal.task.EntityTickTask;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

public final class EntityRegister implements IRegister<BaseEntity> {
    @Nonnull
    private static final Lazy<EntityRegister> inst = Lazy.create(EntityRegister::new);

    @Nonnull
    private final ConcurrentHashMap<NamespacedKey, BaseEntity> lookupTable = new ConcurrentHashMap<>();

    private EntityRegister() {
    }

    @Nonnull
    public static EntityRegister getInstance() {
        BarleyTeaAPI.checkPluginUsable();
        return inst.get();
    }

    @Nullable
    public static EntityRegister getInstanceUnsafe() {
        return inst.getUnsafe();
    }

    @Override
    public void registerAll(@Nullable Collection<BaseEntity> entities) {
        if (entities == null)
            return;
        refreshCustomEntities(
                entities.stream()
                        .map(entity -> RefreshCustomEntityRecord.create(lookupTable.put(entity.getKey(), entity),
                                entity))
                        .filter(Objects::nonNull)
                        .toList());
        if (BarleyTeaAPI.checkPluginUsable()) {
            BarleyTeaAPI inst = BarleyTeaAPI.getInstanceUnsafe();
            if (inst != null) {
                Logger logger = inst.getLogger();
                for (BaseEntity entity : entities)
                    logger.info("registered " + entity.getKey().toString() + " as entity!");
                ObjectUtil.safeCall(inst.summonCommand, NMSRegularCommand::updateSuggestions);
            }
        }
    }

    public void register(@Nullable BaseEntity entity) {
        if (entity == null)
            return;
        refreshCustomEntities(
                List.of(RefreshCustomEntityRecord.create(lookupTable.put(entity.getKey(), entity), entity)));
        if (BarleyTeaAPI.checkPluginUsable()) {
            BarleyTeaAPI inst = BarleyTeaAPI.getInstanceUnsafe();
            if (inst != null) {
                Logger logger = inst.getLogger();
                logger.info("registered " + entity.getKey().toString() + " as entity!");
                if (entity instanceof FeatureNaturalSpawn
                        && !Creature.class.isAssignableFrom(entity.getEntityTypeBasedOn().getEntityClass())) {
                    logger.warning(entity.getKey().toString()
                            + " isn't based on a creature that can be spawned naturally, so FeatureNaturalSpawn won't triggered!");
                }
                if (entity instanceof FeatureSlimeSplit
                        && !Slime.class.isAssignableFrom(entity.getEntityTypeBasedOn().getEntityClass())) {
                    logger.warning(entity.getKey().toString()
                            + " isn't based on a slime-type mob, so FeatureSlimeSplit won't triggered!");
                }
                if (entity instanceof FeatureProjectile
                        && !Projectile.class.isAssignableFrom(entity.getEntityTypeBasedOn().getEntityClass())) {
                    logger.warning(entity.getKey().toString()
                            + " isn't based on a projectile entity, so FeatureProjectile won't triggered!");
                }
                if (entity instanceof FeatureCommandSummon)
                    ObjectUtil.safeCall(inst.summonCommand, NMSRegularCommand::updateSuggestions);
            }
        }
    }

    public void unregister(@Nullable BaseEntity entity) {
        if (entity == null || !lookupTable.remove(entity.getKey(), entity))
            return;
        refreshCustomEntities(
                List.of(RefreshCustomEntityRecord.create(entity, null)));
        BarleyTeaAPI inst = BarleyTeaAPI.getInstanceUnsafe();
        if (inst != null) {
            Logger logger = inst.getLogger();
            logger.info("unregistered " + entity.getKey().toString());
            if (entity instanceof FeatureCommandSummon)
                ObjectUtil.safeCall(inst.summonCommand, NMSRegularCommand::updateSuggestions);
        }
    }

    @Override
    public void unregisterAll() {
        Bukkit.getWorlds().forEach(world -> {
            world.getEntities().forEach(iteratedEntity -> {
                BaseEntity entity = DataEntityType.get(iteratedEntity).asCustomEntity();
                if (entity instanceof FeatureEntityLoad feature)
                    feature.handleEntityUnloaded(iteratedEntity);
                if (entity instanceof FeatureEntityTick) {
                    EntityTickTask task = EntityTickTask.getInstanceUnsafe();
                    if (task != null) {
                        task.removeEntity(iteratedEntity);
                    }
                }
            });
        });
        var keySet = Collections.unmodifiableSet(lookupTable.keySet());
        lookupTable.clear();
        BarleyTeaAPI inst = BarleyTeaAPI.getInstanceUnsafe();
        if (inst != null) {
            Logger logger = inst.getLogger();
            for (NamespacedKey key : keySet) {
                logger.info("unregistered " + key.toString());
            }
            ObjectUtil.safeCall(inst.summonCommand, NMSRegularCommand::updateSuggestions);
        }
    }

    @Override
    public void unregisterAll(@Nullable Predicate<BaseEntity> predicate) {
        if (predicate == null)
            unregisterAll();
        else {
            ArrayList<RefreshCustomEntityRecord> collectingList = new ArrayList<>();
            Logger logger = null;
            BarleyTeaAPI inst = BarleyTeaAPI.getInstanceUnsafe();
            if (inst != null) {
                logger = inst.getLogger();
            }
            for (var iterator = lookupTable.entrySet().iterator(); iterator.hasNext();) {
                var entry = iterator.next();
                NamespacedKey key = entry.getKey();
                BaseEntity entityType = entry.getValue();
                if (predicate.test(entityType)) {
                    iterator.remove();
                    var record = RefreshCustomEntityRecord.create(entityType, null);
                    if (record != null)
                        collectingList.add(record);
                    if (logger != null)
                        logger.info("unregistered " + key.toString());
                }
            }
            refreshCustomEntities(collectingList);
            if (inst != null)
                inst.summonCommand.updateSuggestions();
        }
    }

    @Nullable
    public BaseEntity lookup(@Nullable NamespacedKey key) {
        if (key == null)
            return null;
        return lookupTable.get(key);
    }

    public boolean has(@Nullable NamespacedKey key) {
        if (key == null)
            return false;
        return lookupTable.containsKey(key);
    }

    public boolean hasAnyRegistered() {
        return lookupTable.size() > 0;
    }

    @Override
    @Nonnull
    public Collection<BaseEntity> listAll() {
        return ObjectUtil.letNonNull(Collections.unmodifiableCollection(lookupTable.values()),
                Collections::emptySet);
    }

    @Override
    @Nonnull
    public Collection<BaseEntity> listAll(@Nullable Predicate<BaseEntity> predicate) {
        return predicate == null ? listAll()
                : ObjectUtil.letNonNull(
                        lookupTable.values().stream().filter(predicate).collect(Collectors.toUnmodifiableList()),
                        Collections::emptySet);
    }

    @Override
    @Nonnull
    public Collection<NamespacedKey> listAllKeys() {
        return ObjectUtil.letNonNull(Collections.unmodifiableCollection(lookupTable.keySet()),
                Collections::emptySet);
    }

    @Override
    @Nonnull
    public Collection<NamespacedKey> listAllKeys(@Nullable Predicate<BaseEntity> predicate) {
        return predicate == null ? listAllKeys()
                : ObjectUtil.letNonNull(
                        lookupTable.entrySet().stream().filter(new Filter<>(predicate)).map(new Mapper<>())
                                .collect(Collectors.toUnmodifiableList()),
                        Collections::emptySet);
    }

    @Override
    @Nullable
    public BaseEntity findFirst(@Nullable Predicate<BaseEntity> predicate) {
        var stream = lookupTable.values().stream();
        if (predicate != null)
            stream = stream.filter(predicate);
        return stream.findFirst().orElse(null);
    }

    @Override
    @Nullable
    public NamespacedKey findFirstKey(@Nullable Predicate<BaseEntity> predicate) {
        var stream = lookupTable.entrySet().stream();
        if (predicate != null)
            stream = stream.filter(new Filter<>(predicate));
        return stream.map(new Mapper<>()).findFirst().orElse(null);
    }

    private record RefreshCustomEntityRecord(@Nullable NamespacedKey key,
            @Nullable FeatureEntityLoad oldFeature, @Nullable FeatureEntityLoad newFeature,
            boolean hasTickingOld, boolean hasTickingNew) {

        @Nullable
        public static RefreshCustomEntityRecord create(@Nullable BaseEntity oldBlock, @Nullable BaseEntity newBlock) {
            BaseEntity compareBlock = newBlock == null ? oldBlock : newBlock;
            if (compareBlock == null)
                return null;
            return new RefreshCustomEntityRecord(compareBlock.getKey(),
                    ObjectUtil.tryCast(oldBlock, FeatureEntityLoad.class),
                    ObjectUtil.tryCast(newBlock, FeatureEntityLoad.class),
                    oldBlock instanceof FeatureEntityTick,
                    newBlock instanceof FeatureEntityTick);
        }

        public boolean needOperate() {
            return hasTickingOld || hasTickingNew || oldFeature != null || newFeature != null;
        }
    }

    private void refreshCustomEntities(@Nonnull Collection<RefreshCustomEntityRecord> records) {
        if (records.stream().anyMatch(RefreshCustomEntityRecord::needOperate)) {
            for (World world : Bukkit.getWorlds()) {
                for (Entity entity : world.getEntities()) {
                    NamespacedKey key = BaseEntity.getEntityID(entity);
                    if (key == null)
                        continue;
                    records.stream()
                            .filter(record -> record.key().equals(key))
                            .findAny()
                            .ifPresent(record -> {
                                BarleyTeaAPI plugin = BarleyTeaAPI.getInstanceUnsafe();
                                if (plugin != null) {
                                    BukkitScheduler scheduler = Bukkit.getScheduler();
                                    FeatureEntityLoad feature = record.oldFeature();
                                    if (feature != null) {
                                        final FeatureEntityLoad finalFeature = feature;
                                        scheduler.scheduleSyncDelayedTask(plugin,
                                                () -> finalFeature.handleEntityUnloaded(entity));
                                    }
                                    feature = record.newFeature();
                                    if (feature != null) {
                                        final FeatureEntityLoad finalFeature = feature;
                                        scheduler.scheduleSyncDelayedTask(plugin,
                                                () -> finalFeature.handleEntityLoaded(entity));
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
    }
}
