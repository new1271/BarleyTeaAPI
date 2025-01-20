package org.ricetea.barleyteaapi.internal.task;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.entity.CustomEntity;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityTick;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.barleyteaapi.util.EnumUtil;
import org.ricetea.utils.CollectionUtil;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Singleton
@ApiStatus.Internal
public final class EntityTickTask extends LoopTaskBase {

    @Nonnull
    private static final Lazy<EntityTickTask> _inst = Lazy.create(EntityTickTask::new);

    @Nonnull
    private final Map<UUID, Optional<BukkitTask>> tickingTable = new HashMap<>();
    @Nonnull
    private final Map<UUID, Operation> operationTable = new HashMap<>();
    private int lastTick;

    private EntityTickTask() {
        super(50);
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
    public void runLoop() {
        BarleyTeaAPI api = BarleyTeaAPI.getInstanceUnsafe();
        if (api == null || !EntityRegister.hasRegistered()) {
            stop();
            return;
        }
        BukkitScheduler scheduler = Bukkit.getScheduler();
        synchronized (operationTable) {
            CollectionUtil.forEachAndRemoveAll(operationTable.entrySet(), (uuid, operation) -> {
                if (operation == null)
                    return;
                switch (operation) {
                    case ADD -> tickingTable.putIfAbsent(uuid, Optional.empty());
                    case REMOVE -> {
                        Optional<BukkitTask> taskOptional = tickingTable.remove(uuid);
                        if (taskOptional == null)
                            return;
                        taskOptional.ifPresent((task) -> {
                            if (scheduler.isQueued(task.getTaskId())) {
                                task.cancel();
                            }
                        });
                    }
                }
            });
        }
        if (tickingTable.isEmpty()) {
            synchronized (operationTable) {
                if (!operationTable.isEmpty())
                    return;
            }
            stop();
            return;
        }
        int currentTick = Bukkit.getCurrentTick();
        if (currentTick == lastTick)
            return;
        lastTick = currentTick;
        tickingTable.replaceAll((uuid, taskOptional) -> {
            if (taskOptional.isPresent()) {
                BukkitTask task = taskOptional.get();
                if (scheduler.isQueued(task.getTaskId())) {
                    return taskOptional;
                }
            }
            return Optional.of(scheduler.runTask(api, new _Task(uuid)));
        });
    }

    @Override
    public void stop() {
        super.stop();
        tickingTable.clear();
        synchronized (operationTable) {
            operationTable.clear();
        }
    }

    public void addEntity(@Nullable Entity entity) {
        if (entity == null)
            return;
        addEntity(entity.getUniqueId());
    }

    private void addEntity(@Nullable UUID uuid) {
        if (uuid == null || !BarleyTeaAPI.checkPluginUsable())
            return;
        queueOperation(uuid, Operation.ADD);
    }

    public void removeEntity(@Nullable Entity entity) {
        if (entity == null)
            return;
        removeEntity(entity.getUniqueId());
    }

    private void removeEntity(@Nullable UUID uuid) {
        if (uuid == null || !BarleyTeaAPI.checkPluginUsable())
            return;
        queueOperation(uuid, Operation.REMOVE);
    }

    private void queueOperation(@Nonnull UUID uuid, Operation operation) {
        synchronized (operationTable) {
            boolean needStart;
            if (operationTable.isEmpty()) {
                needStart = true;
                operationTable.put(uuid, operation);
            } else {
                needStart = !isStarted();
                operationTable.merge(uuid, operation, EnumUtil::maxOrdinal);
            }
            if (needStart)
                start();
        }
    }

    private enum Operation {
        ADD,
        REMOVE
    }

    private record _Task(@Nonnull UUID uuid) implements Runnable {

        @Override
        public void run() {
            if (doJob())
                return;
            EntityTickTask task = EntityTickTask.getInstanceUnsafe();
            if (task == null)
                return;
            task.removeEntity(uuid);
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
            FeatureEntityTick feature = entityType.getFeature(FeatureEntityTick.class);
            if (feature != null) {
                ObjectUtil.tryCall(entity, feature::handleTick);
                return true;
            }
            return false;
        }
    }
}
