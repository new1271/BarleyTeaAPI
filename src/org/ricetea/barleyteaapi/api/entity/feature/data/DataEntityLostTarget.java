package org.ricetea.barleyteaapi.api.entity.feature.data;

import javax.annotation.Nonnull;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityTargetEvent;
import org.ricetea.barleyteaapi.api.abstracts.DataEntityBase;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.barleyteaapi.util.Lazy;

public final class DataEntityLostTarget extends DataEntityBase<EntityTargetEvent> {
    @Nonnull
    private final Lazy<DataEntityType> entityType;

    @SuppressWarnings("null")
    public DataEntityLostTarget(@Nonnull EntityTargetEvent event) {
        super(event);
        entityType = new Lazy<>(() -> BaseEntity.getEntityType(event.getEntity()));
    }

    @SuppressWarnings("null")
    @Nonnull
    public Entity getEntity() {
        return event.getEntity();
    }

    @Nonnull
    public DataEntityType getEntityType() {
        return entityType.get();
    }

    @SuppressWarnings("null")
    @Nonnull
    public EntityTargetEvent.TargetReason getReason() {
        return event.getReason();
    }
}
