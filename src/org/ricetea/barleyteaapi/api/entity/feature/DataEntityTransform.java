package org.ricetea.barleyteaapi.api.entity.feature;

import java.util.List;

import javax.annotation.Nonnull;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.entity.EntityTransformEvent.TransformReason;
import org.ricetea.barleyteaapi.api.abstracts.DataEntityBase;
import org.ricetea.barleyteaapi.api.entity.BarleyTeaEntityType;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.util.Lazy;

public final class DataEntityTransform extends DataEntityBase<EntityTransformEvent> {

    @Nonnull
    private final Lazy<BarleyTeaEntityType> originalEntityType, transformedEntityType;

    public DataEntityTransform(@Nonnull EntityTransformEvent event) {
        super(event);
        originalEntityType = new Lazy<>(() -> BaseEntity.getEntityType(event.getEntity()));
        transformedEntityType = new Lazy<>(() -> BaseEntity.getEntityType(event.getTransformedEntity()));
    }

    @SuppressWarnings("null")
    @Nonnull
    public Entity getOriginalEntity() {
        return event.getEntity();
    }

    @Nonnull
    public BarleyTeaEntityType getOriginalEntityType() {
        return originalEntityType.get();
    }

    @SuppressWarnings("null")
    @Nonnull
    public Entity getTransformedEntity() {
        return event.getTransformedEntity();
    }

    @Nonnull
    public BarleyTeaEntityType getTransformedEntityType() {
        return transformedEntityType.get();
    }

    @SuppressWarnings("null")
    @Nonnull
    public List<Entity> getTransformedEntities() {
        return event.getTransformedEntities();
    }

    @SuppressWarnings("null")
    @Nonnull
    public TransformReason getTransformReason() {
        return event.getTransformReason();
    }
}
