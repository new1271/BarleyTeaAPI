package org.ricetea.barleyteaapi.internal.task;

import java.util.*;
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
import org.ricetea.utils.Lazy;

public final class EntityTickTask extends AbstractTask {

    @Nonnull
    private static final Lazy<EntityTickTask> _inst = Lazy.create(EntityTickTask::new);

    @Nonnull
    private final Hashtable<Entity, Integer> tickingTable = new Hashtable<>();

    /*
     * Operations
     * 0 = Add
     * 1 = Reset Task Id
     * 2 = Remove
     */

    @Nonnull
    private final Hashtable<Entity, Integer> operationTable = new Hashtable<>();

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
        BarleyTeaAPI api = BarleyTeaAPI.getInstanceUnsafe();
        BukkitScheduler scheduler = Bukkit.getScheduler();
        EntityRegister register = EntityRegister.getInstanceUnsafe();
        if (api == null || scheduler == null || register == null || !register.hasAnyRegistered()) {
            stop();
        } else {
            synchronized (operationTable) {
                operationTable.forEach((entity, op) -> {
                    switch (op) {
                        case 0 -> {
                            tickingTable.putIfAbsent(entity, 0);
                        }
                        case 1 -> {
                            tickingTable.computeIfPresent(entity, (a, b) -> 0);
                        }
                        case 2 -> {
                            tickingTable.remove(entity);
                        }
                    }
                });
                operationTable.clear();
            }
            if (tickingTable.isEmpty()) {
                stop();
            } else {
                tickingTable.replaceAll((entity, taskId) -> {
                    if (taskId != 0)
                        return taskId;
                    if (entity == null || entity.isDead()) {
                        removeEntity(entity);
                        return 0;
                    }
                    NamespacedKey id = BaseEntity.getEntityID(entity);
                    if (id == null) {
                        removeEntity(entity);
                        return 0;
                    }
                    BaseEntity baseEntity = register.lookup(id);
                    if (baseEntity instanceof FeatureEntityTick tickingEntity) {
                        try {
                            return scheduler.scheduleSyncDelayedTask(api,
                                    new _Task(tickingEntity, entity, operationTable));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        removeEntity(entity);
                    }
                    return 0;
                });
            }
        }
    }

    public void addEntity(@Nullable Entity entity) {
        if (entity == null || !BaseEntity.isBarleyTeaEntity(entity) || !BarleyTeaAPI.checkPluginUsable())
            return;
        synchronized (operationTable) {
            operationTable.merge(entity, 0, Math::max);
        }
        if (!isRunning)
            start();
    }

    public void removeEntity(@Nullable Entity entity) {
        if (entity == null || !BarleyTeaAPI.checkPluginUsable())
            return;
        synchronized (operationTable) {
            operationTable.merge(entity, 2, Math::max);
        }
        if (!isRunning)
            start();
    }

    private static class _Task implements Runnable {
        @Nonnull
        private final FeatureEntityTick feature;
        @Nonnull
        private final Entity entity;
        @Nonnull
        private final Hashtable<Entity, Integer> operationTable;

        _Task(@Nonnull FeatureEntityTick feature, @Nonnull Entity entity,
                @Nonnull Hashtable<Entity, Integer> entityOperationTable) {
            this.feature = feature;
            this.entity = entity;
            this.operationTable = entityOperationTable;
        }

        @Override
        public void run() {
            try {
                feature.handleTick(entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
            synchronized (operationTable) {
                operationTable.merge(entity, 1, Math::max);
            }
        }
    }
}
