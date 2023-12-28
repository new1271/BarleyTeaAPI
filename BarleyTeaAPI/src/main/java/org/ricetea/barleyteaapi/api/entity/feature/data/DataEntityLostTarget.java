package org.ricetea.barleyteaapi.api.entity.feature.data;

import org.bukkit.event.entity.EntityTargetEvent;
import org.ricetea.barleyteaapi.api.base.data.BaseEntityFeatureData;

import javax.annotation.Nonnull;

public final class DataEntityLostTarget extends BaseEntityFeatureData<EntityTargetEvent> {
    public DataEntityLostTarget(@Nonnull EntityTargetEvent event) {
        super(event);
    }

    @Nonnull
    public EntityTargetEvent.TargetReason getReason() {
        return event.getReason();
    }
}
