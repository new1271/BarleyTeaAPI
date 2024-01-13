package org.ricetea.barleyteaapi.internal.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityTame;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityTame;
import org.ricetea.barleyteaapi.internal.linker.EntityFeatureLinker;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import javax.inject.Singleton;

@Singleton
@ApiStatus.Internal
public final class EntityTameListener implements Listener {
    private static final Lazy<EntityTameListener> inst = Lazy.create(EntityTameListener::new);

    private EntityTameListener() {
    }

    @Nonnull
    public static EntityTameListener getInstance() {
        return inst.get();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void listenEntityTame(EntityTameEvent event) {
        if (event == null || event.isCancelled())
            return;
        if (!EntityFeatureLinker.doFeatureCancellable(event.getEntity(), event,
                FeatureEntityTame.class, FeatureEntityTame::handleEntityTame, DataEntityTame::new)) {
            event.setCancelled(true);
        }
    }
}
