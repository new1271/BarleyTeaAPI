package org.ricetea.barleyteaapi.api.entity.feature.data;

import javax.annotation.Nonnull;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityTargetEvent;
import org.ricetea.barleyteaapi.api.abstracts.DataEntityBase;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.barleyteaapi.util.Lazy;

public final class DataEntityTarget extends DataEntityBase<EntityTargetEvent> {
    @Nonnull
    private final Lazy<DataEntityType> entityType, targetType;

    public DataEntityTarget(@Nonnull EntityTargetEvent event) {
        super(event);
        entityType = new Lazy<>(() -> BaseEntity.getEntityType(event.getEntity()));
        targetType = new Lazy<>(() -> BaseEntity.getEntityType(event.getTarget()));
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
    public Entity getTarget() {
        return event.getTarget();
    }

    @Nonnull
    public DataEntityType getTargetType() {
        return targetType.get();
    }

    @SuppressWarnings("null")
    @Nonnull
    public EntityTargetEvent.TargetReason getReason() {
        return event.getReason();
    }
}
