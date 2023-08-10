package org.ricetea.barleyteaapi.internal.listener;

import javax.annotation.Nonnull;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityMount;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityDismount;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityMount;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.barleyteaapi.util.Lazy;
import org.spigotmc.event.entity.EntityDismountEvent;
import org.spigotmc.event.entity.EntityMountEvent;

public final class EntityMountListener implements Listener {
    private static final Lazy<EntityMountListener> inst = new Lazy<>(EntityMountListener::new);

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
        NamespacedKey id = BaseEntity.getEntityID(event.getEntity());
        if (id != null) {
            BaseEntity entity = EntityRegister.getInstance().lookupEntityType(id);
            if (entity != null && entity instanceof FeatureEntityMount) {
                FeatureEntityMount entityMount = (FeatureEntityMount) entity;
                boolean cancelled = !entityMount.handleEntityMount(new DataEntityMount(event));
                if (cancelled) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
        id = BaseEntity.getEntityID(event.getMount());
        if (id != null) {
            BaseEntity entity = EntityRegister.getInstance().lookupEntityType(id);
            if (entity != null && entity instanceof FeatureEntityMount) {
                FeatureEntityMount entityMount = (FeatureEntityMount) entity;
                boolean cancelled = !entityMount.handleEntityBeMounted(new DataEntityMount(event));
                if (cancelled) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDismount(@Nonnull EntityDismountEvent event) {
        if (event == null || event.isCancelled())
            return;
        NamespacedKey id = BaseEntity.getEntityID(event.getEntity());
        if (id != null) {
            BaseEntity entity = EntityRegister.getInstance().lookupEntityType(id);
            if (entity != null && entity instanceof FeatureEntityMount) {
                FeatureEntityMount entityMount = (FeatureEntityMount) entity;
                boolean cancelled = !entityMount.handleEntityDismount(new DataEntityDismount(event));
                if (cancelled) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
        id = BaseEntity.getEntityID(event.getDismounted());
        if (id != null) {
            BaseEntity entity = EntityRegister.getInstance().lookupEntityType(id);
            if (entity != null && entity instanceof FeatureEntityMount) {
                FeatureEntityMount entityMount = (FeatureEntityMount) entity;
                boolean cancelled = !entityMount.handleEntityBeDismounted(new DataEntityDismount(event));
                if (cancelled) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
}
