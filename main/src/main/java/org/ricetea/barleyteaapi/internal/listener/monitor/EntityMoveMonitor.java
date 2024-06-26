package org.ricetea.barleyteaapi.internal.listener.monitor;

import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.entity.feature.EntityFeature;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityMove;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityMount;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityMove;
import org.ricetea.barleyteaapi.api.entity.feature.monitor.FeatureMonitorEntityMove;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemHoldEntityMove;
import org.ricetea.barleyteaapi.api.item.feature.data.DataItemHoldEntityMove;
import org.ricetea.barleyteaapi.api.item.registration.ItemRegister;
import org.ricetea.barleyteaapi.internal.linker.EntityFeatureLinker;
import org.ricetea.barleyteaapi.internal.linker.ItemFeatureLinker;
import org.ricetea.utils.Constants;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import javax.inject.Singleton;

@Singleton
@ApiStatus.Internal
public final class EntityMoveMonitor implements Listener {
    private static final Lazy<EntityMoveMonitor> inst = Lazy.create(EntityMoveMonitor::new);

    private EntityMoveMonitor() {
    }

    @Nonnull
    public static EntityMoveMonitor getInstance() {
        return inst.get();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void listenEntityMove(EntityMoveEvent event) {
        if (event == null || event.isCancelled())
            return;
        EntityFeatureLinker.doFeature(event.getEntity(), event, FeatureMonitorEntityMove.class,
                FeatureMonitorEntityMove::monitorEntityMove, DataEntityMove::new);
    }
}
