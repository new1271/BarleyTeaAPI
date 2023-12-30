package org.ricetea.barleyteaapi.internal.entity.counter;

import com.google.common.collect.HashMultimap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.entity.counter.TickCounter;
import org.ricetea.barleyteaapi.api.entity.counter.TickingService;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.UnsafeHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Singleton;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
@ThreadSafe
@ApiStatus.Internal
public final class SyncTickingServiceImpl implements TickingService, Runnable {

    @Nonnull
    private static final Lazy<SyncTickingServiceImpl> _inst = Lazy.create(SyncTickingServiceImpl::new);

    @Nonnull
    private final Set<Entity> entitiesPrepareToRemove = ConcurrentHashMap.newKeySet();

    @Nonnull
    private final ConcurrentHashMap<Map.Entry<Entity, TickCounter>, Integer> operationTable = new ConcurrentHashMap<>();

    @Nonnull
    private final HashMultimap<Entity, TickCounter> tickingTable = HashMultimap.create();

    @Nonnull
    private final Object syncRoot = new Object();

    @Nullable
    private BukkitTask task;


    private SyncTickingServiceImpl() {
    }

    @Nonnull
    public static SyncTickingServiceImpl getInstance() {
        return _inst.get();
    }

    @Nullable
    public static SyncTickingServiceImpl getInstanceUnsafe() {
        return _inst.getUnsafe();
    }

    private void startIfRequired() {
        if (BarleyTeaAPI.checkPluginUsable()) {
            if (task == null) {
                synchronized (syncRoot) {
                    UnsafeHelper.getUnsafe().fullFence();
                    if (task == null) {
                        task = Bukkit.getScheduler().runTaskTimer(BarleyTeaAPI.getInstance(), this, 1, 1);
                    }
                }
            }
        } else {
            shutdown();
        }
    }

    @Override
    public void addCounter(@Nonnull Entity entity, @Nonnull TickCounter counter) {
        operationTable.merge(new AbstractMap.SimpleImmutableEntry<>(entity, counter), 0, Math::max);
        startIfRequired();
    }

    @Override
    public void removeCounter(@Nonnull Entity entity, @Nonnull TickCounter counter) {
        operationTable.merge(new AbstractMap.SimpleImmutableEntry<>(entity, counter), 1, Math::max);
        startIfRequired();
    }

    @Override
    public void clearCounter(@Nonnull Entity entity) {
        entitiesPrepareToRemove.add(entity);
        startIfRequired();
    }

    @Override
    public void shutdown() {
        if (task != null) {
            synchronized (syncRoot) {
                UnsafeHelper.getUnsafe().fullFence();
                BukkitTask task = this.task;
                if (task != null) {
                    task.cancel();
                    this.task = null;
                    entitiesPrepareToRemove.clear();
                    operationTable.clear();
                    tickingTable.clear();
                }
            }
        }
    }

    @Override
    public void run() {
        for (var iterator = operationTable.entrySet().iterator(); iterator.hasNext(); iterator.remove()) {
            Map.Entry<Map.Entry<Entity, TickCounter>, Integer> entry = iterator.next();
            Map.Entry<Entity, TickCounter> entityEntry = entry.getKey();
            Integer op = entry.getValue();
            if (op == null)
                continue;
            switch (op) {
                case 0 -> tickingTable.put(entityEntry.getKey(), entityEntry.getValue());
                case 1 -> tickingTable.remove(entityEntry.getKey(), entityEntry.getValue());
            }
        }
        for (var iterator = entitiesPrepareToRemove.iterator(); iterator.hasNext(); iterator.remove()) {
            iterator.next();
        }
        tickingTable.asMap().forEach((entity, counters) -> {
            if (entity == null)
                return;
            if (entity.isDead()) {
                clearCounter(entity);
                return;
            }
            counters.forEach(counter -> counter.tick(entity));
        });
    }
}
