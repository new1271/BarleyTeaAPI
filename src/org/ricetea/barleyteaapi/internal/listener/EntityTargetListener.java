package org.ricetea.barleyteaapi.internal.listener;

import javax.annotation.Nonnull;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityTarget;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityBeTargeted;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityLostTarget;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityTarget;
import org.ricetea.barleyteaapi.internal.helper.EntityFeatureHelper;
import org.ricetea.barleyteaapi.util.Lazy;

public final class EntityTargetListener implements Listener {
    private static final Lazy<EntityTargetListener> inst = new Lazy<>(EntityTargetListener::new);

    private EntityTargetListener() {
    }

    @Nonnull
    public static EntityTargetListener getInstance() {
        return inst.get();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
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
        if (!EntityFeatureHelper.doFeatureCancellable(event.getEntity(), event, FeatureEntityTarget.class,
                FeatureEntityTarget::handleEntityTarget, DataEntityTarget::new)) {
            event.setCancelled(true);
            return;
        }
        if (!EntityFeatureHelper.doFeatureCancellable(event.getTarget(), event, FeatureEntityTarget.class,
                FeatureEntityTarget::handleEntityBeTargeted, DataEntityBeTargeted::new)) {
            event.setCancelled(true);
            return;
        }
    }

    private void onEntityLostTarget(@Nonnull EntityTargetEvent event) {
        if (!EntityFeatureHelper.doFeatureCancellable(event.getEntity(), event, FeatureEntityTarget.class,
                FeatureEntityTarget::handleEntityLostTarget, DataEntityLostTarget::new)) {
            event.setCancelled(true);
            return;
        }
    }
}
