package org.ricetea.barleyteaapi.internal.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityMount;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityBeDismounted;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityBeMounted;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityDismount;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityMount;
import org.ricetea.barleyteaapi.internal.linker.EntityFeatureLinker;
import org.ricetea.utils.Lazy;
import org.spigotmc.event.entity.EntityDismountEvent;
import org.spigotmc.event.entity.EntityMountEvent;

import javax.annotation.Nonnull;

public final class EntityMountListener implements Listener {
    private static final Lazy<EntityMountListener> inst = Lazy.create(EntityMountListener::new);

    private EntityMountListener() {
    }

    @Nonnull
    public static EntityMountListener getInstance() {
        return inst.get();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenEntityMount(EntityMountEvent event) {
        if (event == null || event.isCancelled())
            return;
        if (!EntityFeatureLinker.doFeatureCancellable(event.getEntity(), event, FeatureEntityMount.class,
                FeatureEntityMount::handleEntityMount, DataEntityMount::new)) {
            event.setCancelled(true);
            return;
        }
        if (!EntityFeatureLinker.doFeatureCancellable(event.getMount(), event, FeatureEntityMount.class,
                FeatureEntityMount::handleEntityBeMounted, DataEntityBeMounted::new)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenEntityDismount(@Nonnull EntityDismountEvent event) {
        if (event.isCancelled())
            return;
        if (!EntityFeatureLinker.doFeatureCancellable(event.getEntity(), event, FeatureEntityMount.class,
                FeatureEntityMount::handleEntityDismount, DataEntityDismount::new)) {
            event.setCancelled(true);
            return;
        }
        if (!EntityFeatureLinker.doFeatureCancellable(event.getDismounted(), event, FeatureEntityMount.class,
                FeatureEntityMount::handleEntityBeDismounted, DataEntityBeDismounted::new)) {
            event.setCancelled(true);
        }
    }
}
