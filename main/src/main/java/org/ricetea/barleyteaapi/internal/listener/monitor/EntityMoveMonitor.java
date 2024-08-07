package org.ricetea.barleyteaapi.internal.listener.monitor;

import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityMove;
import org.ricetea.barleyteaapi.api.entity.feature.monitor.FeatureMonitorEntityMove;
import org.ricetea.barleyteaapi.internal.linker.EntityFeatureLinker;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
@ApiStatus.Internal
public final class EntityMoveMonitor implements Listener {
    //This listener will cause lags, so it need lazy-loading
    private static final Lazy<EntityMoveMonitor> inst = Lazy.create(EntityMoveMonitor::new);

    private final Object _syncRoot = new Object();

    private final AtomicBoolean loaded;

    private final AtomicInteger refCount;

    private EntityMoveMonitor() {
        loaded = new AtomicBoolean(false);
        refCount = new AtomicInteger(0);
    }

    @Nonnull
    public static EntityMoveMonitor getInstance() {
        return inst.get();
    }

    @Nullable
    public static EntityMoveMonitor getInstanceUnsafe() {
        return inst.getUnsafe();
    }

    public void addReference() {
        int refCount = this.refCount.incrementAndGet();
        if (refCount > 0)
            tryRegisterEvents();
    }

    public void removeReference() {
        int refCount = this.refCount.decrementAndGet();
        if (refCount < 0) {
            this.refCount.set(0);
        }
        if (refCount == 0) {
            tryUnregisterEvents();
        }
    }

    private void tryRegisterEvents() {
        if (loaded.get())
            return;
        synchronized (_syncRoot) {
            BarleyTeaAPI api = BarleyTeaAPI.getInstanceUnsafe();
            if (api == null)
                return;
            try {
                Bukkit.getPluginManager().registerEvents(this, api);
                loaded.set(true);
            } catch (Exception ignored) {
            }
        }
    }

    private void tryUnregisterEvents() {
        if (!loaded.get())
            return;
        synchronized (_syncRoot) {
            try {
                HandlerList.unregisterAll(this);
                loaded.set(false);
            } catch (Exception ignored) {
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void listenEntityMove(EntityMoveEvent event) {
        if (event == null || event.isCancelled())
            return;
        EntityFeatureLinker.doFeature(event.getEntity(), event, FeatureMonitorEntityMove.class,
                FeatureMonitorEntityMove::monitorEntityMove, DataEntityMove::new);
    }
}
