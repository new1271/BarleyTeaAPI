package org.ricetea.barleyteaapi.internal.task;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitScheduler;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityTick;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.barleyteaapi.api.task.AbstractTask;
import org.ricetea.barleyteaapi.util.CachedList;
import org.ricetea.barleyteaapi.util.Lazy;

public final class EntityTickTask extends AbstractTask {

    @Nonnull
    private static final Lazy<EntityTickTask> _inst = new Lazy<>(EntityTickTask::new);

    @Nonnull
    private final CachedList<Entity> entities = new CachedList<>(Entity.class);

    private EntityTickTask() {
        super(50, 0);
    }

    @Nullable
    public static EntityTickTask getInstanceUnsafe() {
        return _inst.getUnsafe();
    }

    @Nonnull
    public static EntityTickTask getInstance() {
        return _inst.get();
    }

    @Override
    protected void runInternal() {
        BarleyTeaAPI api = BarleyTeaAPI.getInstance();
        BukkitScheduler scheduler = Bukkit.getScheduler();
        EntityRegister register = EntityRegister.getInstance();
        if (api == null || scheduler == null || !register.hasAnyRegisteredMob()) {
            stop();
        } else {
            Entity[] entities;
            synchronized (this.entities) {
                entities = this.entities.toArrayCasted();
            }
            if (entities != null) {
                for (Entity entity : entities) {
                    if (entity == null || entity.isDead()) {
                        removeEntity(entity);
                    } else {
                        NamespacedKey id = BaseEntity.getEntityID(entity);
                        if (id == null) {
                            removeEntity(entity);
                        } else {
                            BaseEntity baseEntity = register.lookupEntityType(id);
                            if (baseEntity instanceof FeatureEntityTick tickingEntity)
                                scheduler.runTask(api, () -> tickingEntity.handleTick(entity));
                            else
                                removeEntity(entity);
                        }
                    }
                }
            }
        }
    }

    public void addEntity(@Nullable Entity entity) {
        if (entity == null || !BaseEntity.isBarleyTeaEntity(entity) || !BarleyTeaAPI.checkPluginUsable())
            return;
        synchronized (entities) {
            if (!entities.contains(entity))
                entities.add(entity);
            if (!isRunning) {
                start();
            }
        }
    }

    public void removeEntity(@Nullable Entity entity) {
        if (entity == null || !BarleyTeaAPI.checkPluginUsable())
            return;
        synchronized (entities) {
            entities.remove(entity);
            if (entities.size() <= 0) {
                stop();
            }
        }
    }
}
