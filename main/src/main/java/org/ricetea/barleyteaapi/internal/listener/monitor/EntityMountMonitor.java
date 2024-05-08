package org.ricetea.barleyteaapi.internal.listener.monitor;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityMount;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityBeDismounted;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityBeMounted;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityDismount;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityMount;
import org.ricetea.barleyteaapi.api.entity.feature.monitor.FeatureMonitorEntityMount;
import org.ricetea.barleyteaapi.internal.linker.EntityFeatureLinker;
import org.ricetea.utils.Lazy;
import org.spigotmc.event.entity.EntityDismountEvent;
import org.spigotmc.event.entity.EntityMountEvent;

import javax.annotation.Nonnull;
import javax.inject.Singleton;

@Singleton
@ApiStatus.Internal
public final class EntityMountMonitor implements Listener {
    private static final Lazy<EntityMountMonitor> inst = Lazy.create(EntityMountMonitor::new);

    private EntityMountMonitor() {
    }

    @Nonnull
    public static EntityMountMonitor getInstance() {
        return inst.get();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void listenEntityMount(EntityMountEvent event) {
        if (event == null || event.isCancelled())
            return;
        EntityFeatureLinker.doFeature(event.getEntity(), event, FeatureMonitorEntityMount.class,
                FeatureMonitorEntityMount::monitorEntityMount, DataEntityMount::new);
        EntityFeatureLinker.doFeature(event.getMount(), event, FeatureMonitorEntityMount.class,
                FeatureMonitorEntityMount::monitorEntityBeMounted, DataEntityBeMounted::new);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void listenEntityDismount(@Nonnull EntityDismountEvent event) {
        if (event.isCancelled())
            return;
        EntityFeatureLinker.doFeature(event.getEntity(), event, FeatureMonitorEntityMount.class,
                FeatureMonitorEntityMount::monitorEntityDismount, DataEntityDismount::new);
        EntityFeatureLinker.doFeature(event.getDismounted(), event, FeatureMonitorEntityMount.class,
                FeatureMonitorEntityMount::monitorEntityBeDismounted, DataEntityBeDismounted::new);
    }
}
