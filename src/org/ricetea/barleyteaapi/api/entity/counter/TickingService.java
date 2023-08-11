package org.ricetea.barleyteaapi.api.entity.counter;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.task.AbstractTask;
import org.ricetea.barleyteaapi.util.CachedList;
import org.ricetea.barleyteaapi.util.Lazy;

public final class TickingService {

    public static void addEntityWithCounter(@Nullable Entity entity, @Nullable AbstractTickCounter counter) {
        addEntityWithCounter(entity, counter, false);
    }

    public static void addEntityWithCounter(@Nullable Entity entity, @Nullable AbstractTickCounter counter,
            boolean isAsync) {
        if (entity != null && counter != null && BarleyTeaAPI.checkPluginUsable()) {
            if (isAsync) {
                AsyncGlobalTickingTask.getInstance().addEntityWithCounter(entity, counter);
            } else {
                SyncGlobalTickingTask.getInstance().addEntityWithCounter(entity, counter);
            }
        }
    }

    public static void removeEntityWithCounter(@Nullable Entity entity, @Nullable AbstractTickCounter counter) {
        removeEntityWithCounter(entity, counter, false);
    }

    public static void removeEntityWithCounter(@Nullable Entity entity, @Nullable AbstractTickCounter counter,
            boolean isAsync) {
        if (entity != null && counter != null && BarleyTeaAPI.checkPluginUsable()) {
            if (isAsync) {
                AsyncGlobalTickingTask.getInstance().removeEntityWithCounter(entity, counter);
            } else {
                SyncGlobalTickingTask.getInstance().removeEntityWithCounter(entity, counter);
            }
        }
    }

    public static void removeEntity(@Nullable Entity entity) {
        removeEntity(entity, false);
    }

    public static void removeEntity(@Nullable Entity entity, boolean isAsync) {
        if (entity != null && BarleyTeaAPI.checkPluginUsable()) {
            if (isAsync) {
                AsyncGlobalTickingTask.getInstance().removeEntity(entity);
            } else {
                SyncGlobalTickingTask.getInstance().removeEntity(entity);
            }
        }
    }

    public static void shutdown() {
        SyncGlobalTickingTask.shutdown();
        AsyncGlobalTickingTask.shutdown();
    }

    private static final class SyncGlobalTickingTask implements Runnable {
        int taskID;

        @Nonnull
        private static final Lazy<SyncGlobalTickingTask> _inst = new Lazy<>(SyncGlobalTickingTask::new);

        @Nonnull
        private final ConcurrentHashMap<Entity, CachedList<AbstractTickCounter>> entityMap = new ConcurrentHashMap<>();

        private SyncGlobalTickingTask() {
            taskID = 0;
        }

        @Nonnull
        public static final SyncGlobalTickingTask getInstance() {
            return _inst.get();
        }

        @Override
        public void run() {
            for (Iterator<Entry<Entity, CachedList<AbstractTickCounter>>> iterator = entityMap.entrySet()
                    .iterator(); iterator.hasNext();) {
                Entry<Entity, CachedList<AbstractTickCounter>> entry = iterator.next();
                Entity entity = entry.getKey();
                if (entity != null) {
                    AbstractTickCounter[] array = entry.getValue().toArrayCasted();
                    if (array != null) {
                        for (AbstractTickCounter counter : array) {
                            counter.doTick(entity);
                        }
                    }
                }
            }
        }

        public void addEntityWithCounter(@Nonnull Entity entity, @Nonnull AbstractTickCounter counter) {
            CachedList<AbstractTickCounter> list = entityMap.get(entity);
            if (list == null) {
                list = new CachedList<>(AbstractTickCounter.class, 1);
                entityMap.put(entity, list);
            }
            list.add(counter);
            if (taskID == 0)
                taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(BarleyTeaAPI.getInstance(), this, 0, 1);
        }

        public void removeEntityWithCounter(@Nonnull Entity entity, @Nonnull AbstractTickCounter counter) {
            CachedList<AbstractTickCounter> list = entityMap.get(entity);
            if (list != null) {
                list.remove(counter);
                counter.cleanCounter(entity);
            }
        }

        public void removeEntity(@Nonnull Entity entity) {
            entityMap.remove(entity);
        }

        public static void shutdown() {
            SyncGlobalTickingTask inst = _inst.getUnsafe();
            if (inst != null) {
                int taskID = inst.taskID;
                inst.taskID = 0;
                if (taskID != 0)
                    Bukkit.getScheduler().cancelTask(taskID);
            }
        }
    }

    private static final class AsyncGlobalTickingTask extends AbstractTask {
        @Nonnull
        private static final Lazy<AsyncGlobalTickingTask> _inst = new Lazy<>(AsyncGlobalTickingTask::new);

        @Nonnull
        private final ConcurrentHashMap<Entity, CachedList<AbstractTickCounter>> entityMap = new ConcurrentHashMap<>();

        private AsyncGlobalTickingTask() {
            super(50, 0);
        }

        @Nonnull
        public static final AsyncGlobalTickingTask getInstance() {
            return _inst.get();
        }

        @Override
        public void runInternal() {
            for (Iterator<Entry<Entity, CachedList<AbstractTickCounter>>> iterator = entityMap.entrySet()
                    .iterator(); iterator.hasNext();) {
                Entry<Entity, CachedList<AbstractTickCounter>> entry = iterator.next();
                Entity entity = entry.getKey();
                if (entity != null) {
                    AbstractTickCounter[] array = entry.getValue().toArrayCasted();
                    if (array != null) {
                        for (AbstractTickCounter counter : array) {
                            counter.doTick(entity);
                        }
                    }
                }
            }
        }

        public void addEntityWithCounter(@Nonnull Entity entity, @Nonnull AbstractTickCounter counter) {
            CachedList<AbstractTickCounter> list = entityMap.get(entity);
            if (list == null) {
                list = new CachedList<>(AbstractTickCounter.class, 1);
                entityMap.put(entity, list);
            }
            list.add(counter);
            if (!isRunning) {
                start();
            }
        }

        public void removeEntityWithCounter(@Nonnull Entity entity, @Nonnull AbstractTickCounter counter) {
            CachedList<AbstractTickCounter> list = entityMap.get(entity);
            if (list != null) {
                list.remove(counter);
                counter.cleanCounter(entity);
            }
        }

        public void removeEntity(@Nonnull Entity entity) {
            entityMap.remove(entity);
        }

        public static void shutdown() {
            AsyncGlobalTickingTask inst = _inst.getUnsafe();
            if (inst != null && inst.isRunning) {
                inst.stop();
            }
        }

    }
}