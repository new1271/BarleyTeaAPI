package org.ricetea.barleyteaapi.api.entity.feature.data;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityTargetEvent;
import org.ricetea.barleyteaapi.api.base.data.BaseEntityFeatureData;
import org.ricetea.barleyteaapi.api.entity.CustomEntityType;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class DataEntityTarget extends BaseEntityFeatureData<EntityTargetEvent> {
    @Nonnull
    private final Lazy<CustomEntityType> targetType;

    public DataEntityTarget(@Nonnull EntityTargetEvent event) {
        super(event);
        targetType = Lazy.create(() -> CustomEntityType.get(getTarget()));
    }

    @Nonnull
    public Entity getTarget() {
        return Objects.requireNonNull(event.getTarget());
    }

    @Nonnull
    public CustomEntityType getTargetType() {
        return targetType.get();
    }

    @Nonnull
    public EntityTargetEvent.TargetReason getReason() {
        return event.getReason();
    }
}
