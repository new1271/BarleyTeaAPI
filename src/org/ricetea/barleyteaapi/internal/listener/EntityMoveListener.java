package org.ricetea.barleyteaapi.internal.listener;

import javax.annotation.Nonnull;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityMove;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityMove;
import org.ricetea.barleyteaapi.internal.helper.EntityFeatureHelper;
import org.ricetea.utils.Lazy;

import io.papermc.paper.event.entity.EntityMoveEvent;

public final class EntityMoveListener implements Listener {
    private static final Lazy<EntityMoveListener> inst = Lazy.create(EntityMoveListener::new);

    private EntityMoveListener() {
    }

    @Nonnull
    public static EntityMoveListener getInstance() {
        return inst.get();
    }

    @EventHandler
    public void listenEntityMove(EntityMoveEvent event) {
        if (event == null)
            return;
        if (!EntityFeatureHelper.doFeatureCancellable(event.getEntity(), event, FeatureEntityMove.class,
                FeatureEntityMove::handleEntityMove, DataEntityMove::new))
            event.setCancelled(true);
    }
}
