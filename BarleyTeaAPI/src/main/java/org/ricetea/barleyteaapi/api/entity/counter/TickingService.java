package org.ricetea.barleyteaapi.api.entity.counter;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.task.AbstractTask;
import org.ricetea.utils.CachedSet;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

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
        @Nonnull
        private static final Lazy<SyncGlobalTickingTask> _inst = Lazy.create(SyncGlobalTickingTask::new);
        @Nonnull
        private final ConcurrentHashMap<Entity, CachedSet<AbstractTickCounter>> entityMap = new ConcurrentHashMap<>();
        int taskID;

        private SyncGlobalTickingTask() {
            taskID = 0;
        }

        @Nonnull
        public static SyncGlobalTickingTask getInstance() {
            return _inst.get();
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

        @Override
        public void run() {
            for (Iterator<Entry<Entity, CachedSet<AbstractTickCounter>>> iterator = entityMap.entrySet()
                    .iterator(); iterator.hasNext(); ) {
                Entry<Entity, CachedSet<AbstractTickCounter>> entry = iterator.next();
                Entity entity = entry.getKey();
                if (entity == null || entity.isDead()) {
                    iterator.remove();
                } else {
                    CachedSet<AbstractTickCounter> list = entry.getValue();
                    if (list != null) {
                        AbstractTickCounter[] array;
                        synchronized (list) {
                            array = entry.getValue().toArrayCasted();
                        }
                        if (array != null) {
                            for (AbstractTickCounter counter : array) {
                                try {
                                    counter.doTick(entity);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }

        public void addEntityWithCounter(@Nonnull Entity entity, @Nonnull AbstractTickCounter counter) {
            CachedSet<AbstractTickCounter> list = entityMap.get(entity);
            if (list == null) {
                list = new CachedSet<>(AbstractTickCounter.class, 1);
                entityMap.put(entity, list);
            }
            synchronized (list) {
                list.add(counter);
            }
            if (taskID == 0)
                taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(BarleyTeaAPI.getInstance(), this, 0, 1);
        }

        public void removeEntityWithCounter(@Nonnull Entity entity, @Nonnull AbstractTickCounter counter) {
            CachedSet<AbstractTickCounter> list = entityMap.get(entity);
            if (list != null) {
                synchronized (list) {
                    list.remove(counter);
                }
                counter.cleanCounter(entity);
            }
        }

        public void removeEntity(@Nonnull Entity entity) {
            entityMap.remove(entity);
            if (entityMap.size() <= 0 && taskID != 0) {
                Bukkit.getScheduler().cancelTask(taskID);
                taskID = 0;
            }
        }
    }

    private static final class AsyncGlobalTickingTask extends AbstractTask {
        @Nonnull
        private static final Lazy<AsyncGlobalTickingTask> _inst = Lazy.create(AsyncGlobalTickingTask::new);

        @Nonnull
        private final ConcurrentHashMap<Entity, CachedSet<AbstractTickCounter>> entityMap = new ConcurrentHashMap<>();

        private int tickCount;

        private AsyncGlobalTickingTask() {
            super(50, 0);
        }

        @Nonnull
        public static AsyncGlobalTickingTask getInstance() {
            return _inst.get();
        }

        public static void shutdown() {
            AsyncGlobalTickingTask inst = _inst.getUnsafe();
            if (inst != null && inst.isRunning) {
                inst.stop();
            }
        }

        @Override
        public void runInternal() {
            int newTickCount = Bukkit.getCurrentTick();
            if (newTickCount == tickCount) {
                return;
            } else {
                this.tickCount = newTickCount;
            }
            for (Iterator<Entry<Entity, CachedSet<AbstractTickCounter>>> iterator = entityMap.entrySet()
                    .iterator(); iterator.hasNext(); ) {
                Entry<Entity, CachedSet<AbstractTickCounter>> entry = iterator.next();
                Entity entity = entry.getKey();
                if (entity == null || entity.isDead()) {
                    iterator.remove();
                } else {
                    CachedSet<AbstractTickCounter> list = entry.getValue();
                    if (list == null)
                        iterator.remove();
                    else {
                        AbstractTickCounter[] array;
                        synchronized (list) {
                            array = entry.getValue().toArrayCasted();
                        }
                        if (array != null) {
                            for (AbstractTickCounter counter : array) {
                                try {
                                    counter.doTick(entity);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }

        public void addEntityWithCounter(@Nonnull Entity entity, @Nonnull AbstractTickCounter counter) {
            CachedSet<AbstractTickCounter> list = entityMap.get(entity);
            if (list == null) {
                list = new CachedSet<>(AbstractTickCounter.class, 1);
                entityMap.put(entity, list);
            }
            list.add(counter);
            if (!isRunning) {
                start();
            }
        }

        public void removeEntityWithCounter(@Nonnull Entity entity, @Nonnull AbstractTickCounter counter) {
            CachedSet<AbstractTickCounter> list = entityMap.get(entity);
            if (list != null) {
                list.remove(counter);
                counter.cleanCounter(entity);
            }
        }

        public void removeEntity(@Nonnull Entity entity) {
            entityMap.remove(entity);
            if (isRunning && entityMap.size() <= 0) {
                stop();
            }
        }

    }
}
