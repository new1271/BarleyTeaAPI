package org.ricetea.barleyteaapi.internal.listener.monitor;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityTarget;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityBeTargeted;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityLostTarget;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityTame;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityTarget;
import org.ricetea.barleyteaapi.api.entity.feature.monitor.FeatureMonitorEntityTame;
import org.ricetea.barleyteaapi.api.entity.feature.monitor.FeatureMonitorEntityTarget;
import org.ricetea.barleyteaapi.internal.linker.EntityFeatureLinker;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import javax.inject.Singleton;

@Singleton
@ApiStatus.Internal
public final class EntityTargetMonitor implements Listener {
    private static final Lazy<EntityTargetMonitor> inst = Lazy.create(EntityTargetMonitor::new);

    private EntityTargetMonitor() {
    }

    @Nonnull
    public static EntityTargetMonitor getInstance() {
        return inst.get();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void listenEntityTarget(EntityTargetEvent event) {
        if (event == null || event.isCancelled())
            return;
        if (event.getTarget() == null) {
            onEntityLostTarget(event);
        } else {
            onEntityTarget(event);
        }
    }

    private void onEntityTarget(@Nonnull EntityTargetEvent event) {
        EntityFeatureLinker.doFeature(event.getEntity(), event, FeatureMonitorEntityTarget.class,
                FeatureMonitorEntityTarget::monitorEntityTarget, DataEntityTarget::new);
        EntityFeatureLinker.doFeature(event.getTarget(), event, FeatureMonitorEntityTarget.class,
                FeatureMonitorEntityTarget::monitorEntityBeTargeted, DataEntityBeTargeted::new);
    }

    private void onEntityLostTarget(@Nonnull EntityTargetEvent event) {
        EntityFeatureLinker.doFeature(event.getEntity(), event, FeatureMonitorEntityTarget.class,
                FeatureMonitorEntityTarget::monitorEntityLostTarget, DataEntityLostTarget::new);
    }
}
