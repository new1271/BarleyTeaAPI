package org.ricetea.barleyteaapi.api.entity.registration;

import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Slime;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.abstracts.IRegister;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureCommandSummon;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureNaturalSpawn;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureProjectile;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureSlimeSplit;
import org.ricetea.barleyteaapi.internal.nms.NMSBaseCommand;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

public final class EntityRegister implements IRegister<BaseEntity> {
    @Nonnull
    private static final Lazy<EntityRegister> inst = Lazy.create(EntityRegister::new);

    @Nonnull
    private final Hashtable<NamespacedKey, BaseEntity> lookupTable = new Hashtable<>();

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

    public void register(@Nullable BaseEntity entity) {
        if (entity == null)
            return;
        lookupTable.put(entity.getKey(), entity);
        if (BarleyTeaAPI.checkPluginUsable()) {
            BarleyTeaAPI inst = BarleyTeaAPI.getInstance();
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
                    ObjectUtil.callWhenNonnull(inst.summonCommand, NMSBaseCommand::update);
            }
        }

    }

    public void unregister(@Nullable BaseEntity entity) {
        if (entity == null)
            return;
        lookupTable.remove(entity.getKey());
        BarleyTeaAPI inst = BarleyTeaAPI.getInstance();
        if (inst != null) {
            Logger logger = inst.getLogger();
            logger.info("unregistered " + entity.getKey().toString());
            if (entity instanceof FeatureCommandSummon)
                ObjectUtil.callWhenNonnull(inst.summonCommand, NMSBaseCommand::update);
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
}
