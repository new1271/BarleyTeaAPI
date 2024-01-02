package org.ricetea.barleyteaapi.internal.entity.counter;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.entity.counter.TickCounter;
import org.ricetea.barleyteaapi.api.entity.counter.TickingService;
import org.ricetea.barleyteaapi.internal.task.LoopTaskBase;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Singleton;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
@ThreadSafe
@ApiStatus.Internal
public final class AsyncTickingServiceImpl extends LoopTaskBase implements TickingService {

    @Nonnull
    private static final Lazy<AsyncTickingServiceImpl> _inst = Lazy.create(AsyncTickingServiceImpl::new);

    @Nonnull
    private final Set<Entity> entitiesPrepareToRemove = ConcurrentHashMap.newKeySet();

    @Nonnull
    private final ConcurrentHashMap<Map.Entry<Entity, TickCounter>, Integer> operationTable = new ConcurrentHashMap<>();

    @Nonnull
    private final HashMap<Entity, HashMap<TickCounter, Integer>> tickingTable = new HashMap<>();

    @Nonnull
    private final Object syncRoot = new Object();

    private AsyncTickingServiceImpl() {
        super(50);
    }

    @Nonnull
    public static AsyncTickingServiceImpl getInstance() {
        return _inst.get();
    }

    @Nullable
    public static AsyncTickingServiceImpl getInstanceUnsafe() {
        return _inst.getUnsafe();
    }

    @Override
    public void addCounter(@Nonnull Entity entity, @Nonnull TickCounter counter) {
        operationTable.merge(new AbstractMap.SimpleImmutableEntry<>(entity, counter), 0, Math::max);
        start();
    }

    @Override
    public void removeCounter(@Nonnull Entity entity, @Nonnull TickCounter counter) {
        operationTable.merge(new AbstractMap.SimpleImmutableEntry<>(entity, counter), 1, Math::max);
        start();
    }

    @Override
    public void clearCounter(@Nonnull Entity entity) {
        entitiesPrepareToRemove.add(entity);
        start();
    }

    @Override
    public void shutdown() {
        stop();
        synchronized (syncRoot) {
            entitiesPrepareToRemove.clear();
            operationTable.clear();
            tickingTable.clear();
        }
    }

    @Override
    public void runLoop() {
        BukkitScheduler scheduler = Bukkit.getScheduler();
        for (var iterator = operationTable.entrySet().iterator(); iterator.hasNext(); iterator.remove()) {
            Map.Entry<Map.Entry<Entity, TickCounter>, Integer> entry = iterator.next();
            Map.Entry<Entity, TickCounter> entityEntry = entry.getKey();
            Integer op = entry.getValue();
            if (op == null)
                continue;
            HashMap<TickCounter, Integer> map = tickingTable.computeIfAbsent(entityEntry.getKey(), ignored -> new HashMap<>());
            switch (op) {
                case 0 -> map.put(entityEntry.getValue(), 0);
                case 1 -> {
                    Integer id = map.remove(entityEntry.getValue());
                    if (id != null && id != 0)
                        scheduler.cancelTask(id);
                }
            }
        }
        for (var iterator = entitiesPrepareToRemove.iterator(); iterator.hasNext(); iterator.remove()) {
            iterator.next();
        }
        BarleyTeaAPI api = BarleyTeaAPI.getInstanceUnsafe();
        if (tickingTable.isEmpty() || api == null) {
            shutdown();
            return;
        }
        tickingTable.forEach((entity, counterMap) -> {
            if (entity == null)
                return;
            if (entity.isDead()) {
                clearCounter(entity);
                return;
            }
            counterMap.replaceAll((counter, taskId) -> {
                if (taskId != null && taskId != 0
                        && (scheduler.isCurrentlyRunning(taskId) || scheduler.isQueued(taskId)))
                    return taskId;
                return scheduler.scheduleSyncDelayedTask(api, () -> counter.tick(entity));
            });
        });
    }
}
