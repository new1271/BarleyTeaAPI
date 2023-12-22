package org.ricetea.barleyteaapi.api.entity.feature.data;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityTargetEvent;
import org.ricetea.barleyteaapi.api.abstracts.BaseEntityFeatureData;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;

public final class DataEntityBeTargeted extends BaseEntityFeatureData<EntityTargetEvent> {
    @Nonnull
    private final Lazy<DataEntityType> entityWhoTargetingType;

    public DataEntityBeTargeted(@Nonnull EntityTargetEvent event) {
        super(event, event.getTarget());
        entityWhoTargetingType = Lazy.create(() -> BaseEntity.getEntityType(getEntityWhoTargeting()));
    }

    @Nonnull
    public Entity getEntityWhoTargeting() {
        return event.getEntity();
    }

    @Nonnull
    public DataEntityType getEntityWhoTargetingType() {
        return entityWhoTargetingType.get();
    }

    @Nonnull
    public EntityTargetEvent.TargetReason getReason() {
        return event.getReason();
    }
}
