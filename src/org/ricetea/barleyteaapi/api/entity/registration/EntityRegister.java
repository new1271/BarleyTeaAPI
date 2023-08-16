package org.ricetea.barleyteaapi.api.entity.registration;

import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.logging.Logger;

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
import org.ricetea.barleyteaapi.util.Lazy;
import org.ricetea.barleyteaapi.util.ObjectUtil;

public final class EntityRegister implements IRegister<BaseEntity> {
    @Nonnull
    private static final Lazy<EntityRegister> inst = new Lazy<>(EntityRegister::new);

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

    public void register(@Nonnull BaseEntity entity) {
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

    public void unregister(@Nonnull BaseEntity entity) {
        lookupTable.remove(entity.getKey());
        BarleyTeaAPI inst = BarleyTeaAPI.getInstance();
        if (inst != null) {
            if (entity instanceof FeatureCommandSummon)
                ObjectUtil.callWhenNonnull(inst.summonCommand, NMSBaseCommand::update);
        }
    }

    @Nullable
    public BaseEntity lookupEntityType(@Nonnull NamespacedKey key) {
        return lookupTable.get(key);
    }

    public boolean hasEntityType(@Nonnull NamespacedKey key) {
        return lookupTable.containsKey(key);
    }

    public boolean hasAnyRegisteredMob() {
        return lookupTable.size() > 0;
    }

    @Nonnull
    public NamespacedKey[] getEntityIDs(@Nullable Predicate<BaseEntity> filter) {
        NamespacedKey[] result;
        if (filter == null)
            result = lookupTable.keySet().toArray(NamespacedKey[]::new);
        else
            result = lookupTable.entrySet().stream().filter(new EntityFilter(filter)).map(e -> e.getKey())
                    .toArray(NamespacedKey[]::new);
        return result != null ? result : new NamespacedKey[0];
    }

    @Nonnull
    public BaseEntity[] getEntityTypes(@Nullable Predicate<BaseEntity> filter) {
        BaseEntity[] result;
        if (filter == null)
            result = lookupTable.values().toArray(BaseEntity[]::new);
        else
            result = lookupTable.values().stream().filter(filter).toArray(BaseEntity[]::new);
        return result != null ? result : new BaseEntity[0];
    }

    private static class EntityFilter implements Predicate<Map.Entry<NamespacedKey, BaseEntity>> {

        @Nonnull
        Predicate<BaseEntity> filter;

        public EntityFilter(@Nonnull Predicate<BaseEntity> filter) {
            this.filter = filter;
        }

        @Override
        public boolean test(Entry<NamespacedKey, BaseEntity> t) {
            return filter.test(t.getValue());
        }

    }
}
