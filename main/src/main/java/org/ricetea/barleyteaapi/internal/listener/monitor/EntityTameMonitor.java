package org.ricetea.barleyteaapi.internal.listener.monitor;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityTame;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityMove;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityTame;
import org.ricetea.barleyteaapi.api.entity.feature.monitor.FeatureMonitorEntityMove;
import org.ricetea.barleyteaapi.api.entity.feature.monitor.FeatureMonitorEntityTame;
import org.ricetea.barleyteaapi.internal.linker.EntityFeatureLinker;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import javax.inject.Singleton;

@Singleton
@ApiStatus.Internal
public final class EntityTameMonitor implements Listener {
    private static final Lazy<EntityTameMonitor> inst = Lazy.create(EntityTameMonitor::new);

    private EntityTameMonitor() {
    }

    @Nonnull
    public static EntityTameMonitor getInstance() {
        return inst.get();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void listenEntityTame(EntityTameEvent event) {
        if (event == null || event.isCancelled())
            return;
        EntityFeatureLinker.doFeature(event.getEntity(), event, FeatureMonitorEntityTame.class,
                FeatureMonitorEntityTame::monitorEntityTame, DataEntityTame::new);
    }
}
