package org.ricetea.barleyteaapi.internal.listener.monitor;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTransformEvent;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityTransform;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityTarget;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityTransform;
import org.ricetea.barleyteaapi.api.entity.feature.monitor.FeatureMonitorEntityTarget;
import org.ricetea.barleyteaapi.api.entity.feature.monitor.FeatureMonitorEntityTransform;
import org.ricetea.barleyteaapi.internal.linker.EntityFeatureLinker;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import javax.inject.Singleton;

@Singleton
@ApiStatus.Internal
public final class EntityTransformMonitor implements Listener {
    private static final Lazy<EntityTransformMonitor> inst = Lazy.create(EntityTransformMonitor::new);

    private EntityTransformMonitor() {
    }

    @Nonnull
    public static EntityTransformMonitor getInstance() {
        return inst.get();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void listenEntityTransform(EntityTransformEvent event) {
        if (event == null || event.isCancelled())
            return;
        EntityFeatureLinker.doFeature(event.getEntity(), event, FeatureMonitorEntityTransform.class,
                FeatureMonitorEntityTransform::monitorEntityTransform, DataEntityTransform::new);
    }
}
