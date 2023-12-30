package org.ricetea.barleyteaapi.internal.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityTarget;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityBeTargeted;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityLostTarget;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityTarget;
import org.ricetea.barleyteaapi.internal.linker.EntityFeatureLinker;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import javax.inject.Singleton;

@Singleton
@ApiStatus.Internal
public final class EntityTargetListener implements Listener {
    private static final Lazy<EntityTargetListener> inst = Lazy.create(EntityTargetListener::new);

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
        if (!EntityFeatureLinker.doFeatureCancellable(event.getEntity(), event, FeatureEntityTarget.class,
                FeatureEntityTarget::handleEntityTarget, DataEntityTarget::new)) {
            event.setCancelled(true);
            return;
        }
        if (!EntityFeatureLinker.doFeatureCancellable(event.getTarget(), event, FeatureEntityTarget.class,
                FeatureEntityTarget::handleEntityBeTargeted, DataEntityBeTargeted::new)) {
            event.setCancelled(true);
        }
    }

    private void onEntityLostTarget(@Nonnull EntityTargetEvent event) {
        if (!EntityFeatureLinker.doFeatureCancellable(event.getEntity(), event, FeatureEntityTarget.class,
                FeatureEntityTarget::handleEntityLostTarget, DataEntityLostTarget::new)) {
            event.setCancelled(true);
        }
    }
}
