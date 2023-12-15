package org.ricetea.barleyteaapi.api.entity.feature.data;

import java.util.Objects;

import javax.annotation.Nonnull;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityTargetEvent;
import org.ricetea.barleyteaapi.api.abstracts.BaseEntityFeatureData;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.utils.Lazy;

public final class DataEntityTarget extends BaseEntityFeatureData<EntityTargetEvent> {
    @Nonnull
    private final Lazy<DataEntityType> targetType;

    public DataEntityTarget(@Nonnull EntityTargetEvent event) {
        super(event);
        targetType = Lazy.create(() -> BaseEntity.getEntityType(getTarget()));
    }

    @Nonnull
    public Entity getTarget() {
        return Objects.requireNonNull(event.getTarget());
    }

    @Nonnull
    public DataEntityType getTargetType() {
        return targetType.get();
    }

    @Nonnull
    public EntityTargetEvent.TargetReason getReason() {
        return event.getReason();
    }
}
