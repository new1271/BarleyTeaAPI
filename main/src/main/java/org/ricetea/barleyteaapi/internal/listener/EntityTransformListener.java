package org.ricetea.barleyteaapi.internal.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTransformEvent;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityTransform;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityTransform;
import org.ricetea.barleyteaapi.internal.linker.EntityFeatureLinker;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import javax.inject.Singleton;

@Singleton
@ApiStatus.Internal
public final class EntityTransformListener implements Listener {
    private static final Lazy<EntityTransformListener> inst = Lazy.create(EntityTransformListener::new);

    private EntityTransformListener() {
    }

    @Nonnull
    public static EntityTransformListener getInstance() {
        return inst.get();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void listenEntityTransform(EntityTransformEvent event) {
        if (event == null || event.isCancelled())
            return;
        if (!EntityFeatureLinker.doFeatureCancellable(event.getEntity(), event, FeatureEntityTransform.class,
                FeatureEntityTransform::handleEntityTransform, DataEntityTransform::new)) {
            event.setCancelled(true);
        }
    }
}
