package org.ricetea.barleyteaapi.api.entity.feature.data;

import java.util.List;

import javax.annotation.Nonnull;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.entity.EntityTransformEvent.TransformReason;
import org.ricetea.barleyteaapi.api.abstracts.BaseEntityFeatureData;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.utils.Lazy;

public final class DataEntityTransform extends BaseEntityFeatureData<EntityTransformEvent> {

    @Nonnull
    private final Lazy<DataEntityType> transformedEntityType;

    public DataEntityTransform(@Nonnull EntityTransformEvent event) {
        super(event);
        transformedEntityType = Lazy.create(() -> BaseEntity.getEntityType(getTransformedEntity()));
    }

    @Nonnull
    public Entity getTransformedEntity() {
        return event.getTransformedEntity();
    }

    @Nonnull
    public DataEntityType getTransformedEntityType() {
        return transformedEntityType.get();
    }

    @Nonnull
    public List<Entity> getTransformedEntities() {
        return event.getTransformedEntities();
    }

    @Nonnull
    public TransformReason getTransformReason() {
        return event.getTransformReason();
    }
}
