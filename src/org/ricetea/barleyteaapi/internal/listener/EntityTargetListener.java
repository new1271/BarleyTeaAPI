package org.ricetea.barleyteaapi.internal.listener;

import javax.annotation.Nonnull;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityTarget;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityLostTarget;
import org.ricetea.barleyteaapi.api.entity.feature.data.DataEntityTarget;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
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
        NamespacedKey id = BaseEntity.getEntityID(event.getEntity());
        if (id != null) {
            BaseEntity entity = EntityRegister.getInstance().lookupEntityType(id);
            if (entity instanceof FeatureEntityTarget entityTarget) {
                boolean cancelled = !entityTarget.handleEntityTarget(new DataEntityTarget(event));
                if (cancelled) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
        id = BaseEntity.getEntityID(event.getTarget());
        if (id != null) {
            BaseEntity entity = EntityRegister.getInstance().lookupEntityType(id);
            if (entity instanceof FeatureEntityTarget entityTarget) {
                boolean cancelled = !entityTarget.handleEntityBeTargeted(new DataEntityTarget(event));
                if (cancelled) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    private void onEntityLostTarget(@Nonnull EntityTargetEvent event) {
        NamespacedKey id = BaseEntity.getEntityID(event.getEntity());
        if (id != null) {
            BaseEntity entity = EntityRegister.getInstance().lookupEntityType(id);
            if (entity instanceof FeatureEntityTarget entityTarget) {
                boolean cancelled = !entityTarget.handleEntityLostTarget(new DataEntityLostTarget(event));
                if (cancelled) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
}
