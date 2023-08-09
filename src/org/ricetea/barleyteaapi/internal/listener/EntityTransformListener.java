package org.ricetea.barleyteaapi.internal.listener;

import javax.annotation.Nonnull;

import org.bukkit.NamespacedKey;
import org.bukkit.event.entity.EntityTransformEvent;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.feature.DataEntityTransform;
import org.ricetea.barleyteaapi.api.entity.feature.FeatureEntityTransform;
import org.ricetea.barleyteaapi.api.entity.registration.EntityRegister;
import org.ricetea.barleyteaapi.util.Lazy;

public class EntityTransformListener {
    private static final Lazy<EntityTransformListener> inst = new Lazy<>(EntityTransformListener::new);

    private EntityTransformListener() {
    }

    @Nonnull
    public static EntityTransformListener getInstance() {
        return inst.get();
    }

    public void onEntityTransform(@Nonnull EntityTransformEvent event) {
        if (event == null || event.isCancelled())
            return;
        NamespacedKey id = BaseEntity.getEntityID(event.getEntity());
        if (id != null) {
            BaseEntity entity = EntityRegister.getInstance().lookupEntityType(id);
            if (entity != null && entity instanceof FeatureEntityTransform) {
                FeatureEntityTransform entityTransform = (FeatureEntityTransform) entity;
                boolean cancelled = !entityTransform.handleEntityTransform(new DataEntityTransform(event));
                if (cancelled) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
}