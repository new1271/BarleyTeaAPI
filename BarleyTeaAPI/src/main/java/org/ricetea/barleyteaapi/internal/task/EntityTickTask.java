package org.ricetea.barleyteaapi.internal.task;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitScheduler;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.entity.CustomEntity;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityTick;
import org.ricetea.barleyteaapi.api.entity.helper.EntityHelper;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.barleyteaapi.api.task.AbstractTask;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class EntityTickTask extends AbstractTask {

    @Nonnull
    private static final Lazy<EntityTickTask> _inst = Lazy.create(EntityTickTask::new);

    @Nonnull
    private final Hashtable<UUID, Integer> tickingTable = new Hashtable<>();
    @Nonnull
    private final ConcurrentHashMap<UUID, Integer> operationTable = new ConcurrentHashMap<>();

    /*
     * Operations
     * 0 = Add
     * 1 = Remove
     */
    private int lastTick;

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
        if (api == null || !EntityRegister.hasRegistered()) {
            stop();
        } else {
            BukkitScheduler scheduler = Bukkit.getScheduler();
            for (var iterator = operationTable.entrySet().iterator(); iterator.hasNext(); iterator.remove()) {
                var entry = iterator.next();
                UUID uuid = entry.getKey();
                Integer op = entry.getValue();
                if (op == null)
                    continue;
                switch (op) {
                    case 0 -> tickingTable.putIfAbsent(uuid, 0);
                    case 1 -> {
                        Integer id = tickingTable.remove(uuid);
                        if (id != null && id != 0)
                            scheduler.cancelTask(id);
                    }
                }
            }
            if (tickingTable.isEmpty()) {
                stop();
            } else {
                int currentTick = Bukkit.getCurrentTick();
                if (currentTick != lastTick) {
                    lastTick = currentTick;
                    tickingTable.replaceAll((uuid, taskId) -> {
                        if (taskId != null && taskId != 0
                                && (scheduler.isCurrentlyRunning(taskId) || scheduler.isQueued(taskId)))
                            return taskId;
                        return scheduler.scheduleSyncDelayedTask(api, new _Task(uuid, operationTable));
                    });
                }
            }
        }

    }

    @Override
    public void stop() {
        super.stop();
        tickingTable.clear();
        operationTable.clear();
    }

    public void addEntity(@Nullable Entity entity) {
        if (!EntityHelper.isCustomEntity(entity) || !BarleyTeaAPI.checkPluginUsable())
            return;
        operationTable.merge(entity.getUniqueId(), 0, Math::max);
        if (!isRunning)
            start();
    }

    public void removeEntity(@Nullable Entity entity) {
        if (entity == null || !BarleyTeaAPI.checkPluginUsable())
            return;
        operationTable.merge(entity.getUniqueId(), 1, Math::max);
        if (!isRunning)
            start();
    }

    private static class _Task implements Runnable {
        @Nonnull
        private final UUID uuid;
        @Nonnull
        private final Map<UUID, Integer> operationTable;

        _Task(@Nonnull UUID uuid, @Nonnull Map<UUID, Integer> entityOperationTable) {
            this.uuid = uuid;
            this.operationTable = entityOperationTable;
        }

        @Override
        public void run() {
            if (!doJob())
                operationTable.merge(uuid, 1, Math::max);
        }

        private boolean doJob() {
            if (!EntityRegister.hasRegistered())
                return false;
            Entity entity = Bukkit.getEntity(uuid);
            if (entity == null || entity.isDead())
                return false;
            CustomEntity entityType = CustomEntity.get(entity);
            if (entityType == null)
                return true;
            if (entityType instanceof FeatureEntityTick feature) {
                try {
                    feature.handleTick(entity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
            return false;
        }
    }
}
