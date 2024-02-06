package org.ricetea.barleyteaapi.api.entity.feature.data;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.entity.EntityTransformEvent.TransformReason;
import org.ricetea.barleyteaapi.api.base.data.BaseEntityFeatureData;
import org.ricetea.barleyteaapi.api.entity.CustomEntityType;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import java.util.List;

public final class DataEntityTransform extends BaseEntityFeatureData<EntityTransformEvent> {

    @Nonnull
    private final Lazy<CustomEntityType> transformedEntityType;

    public DataEntityTransform(@Nonnull EntityTransformEvent event) {
        super(event);
        transformedEntityType = Lazy.create(() -> CustomEntityType.get(getTransformedEntity()));
    }

    @Nonnull
    public Entity getTransformedEntity() {
        return event.getTransformedEntity();
    }

    @Nonnull
    public CustomEntityType getTransformedEntityType() {
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
