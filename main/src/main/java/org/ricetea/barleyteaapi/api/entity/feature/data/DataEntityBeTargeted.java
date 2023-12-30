package org.ricetea.barleyteaapi.api.entity.feature.data;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityTargetEvent;
import org.ricetea.barleyteaapi.api.base.data.BaseEntityFeatureData;
import org.ricetea.barleyteaapi.api.entity.CustomEntityType;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;

public final class DataEntityBeTargeted extends BaseEntityFeatureData<EntityTargetEvent> {
    @Nonnull
    private final Lazy<CustomEntityType> entityWhoTargetingType;

    public DataEntityBeTargeted(@Nonnull EntityTargetEvent event) {
        super(event, event.getTarget());
        entityWhoTargetingType = Lazy.create(() -> CustomEntityType.get(getEntityWhoTargeting()));
    }

    @Nonnull
    public Entity getEntityWhoTargeting() {
        return event.getEntity();
    }

    @Nonnull
    public CustomEntityType getEntityWhoTargetingType() {
        return entityWhoTargetingType.get();
    }

    @Nonnull
    public EntityTargetEvent.TargetReason getReason() {
        return event.getReason();
    }
}
