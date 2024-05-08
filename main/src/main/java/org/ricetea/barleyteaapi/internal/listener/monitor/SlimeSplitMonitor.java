package org.ricetea.barleyteaapi.internal.listener.monitor;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureSlimeSplit;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityShotEntity;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataSlimeSplit;
import org.ricetea.barleyteaapi.api.entity.feature.monitor.FeatureMonitorEntityShoot;
import org.ricetea.barleyteaapi.api.entity.feature.monitor.FeatureMonitorSlimeSplit;
import org.ricetea.barleyteaapi.internal.linker.EntityFeatureLinker;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import javax.inject.Singleton;

@Singleton
@ApiStatus.Internal
public final class SlimeSplitMonitor implements Listener {
    private static final Lazy<SlimeSplitMonitor> inst = Lazy.create(SlimeSplitMonitor::new);

    private SlimeSplitMonitor() {
    }

    @Nonnull
    public static SlimeSplitMonitor getInstance() {
        return inst.get();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void listenSlimeSplit(SlimeSplitEvent event) {
        if (event == null || event.isCancelled())
            return;
        EntityFeatureLinker.doFeature(event.getEntity(), event, FeatureMonitorSlimeSplit.class,
                FeatureMonitorSlimeSplit::monitorSlimeSplit, DataSlimeSplit::new);
    }
}
