package org.ricetea.barleyteaapi.api.entity.feature.data;

import javax.annotation.Nonnull;

import org.bukkit.event.entity.EntityTargetEvent;
import org.ricetea.barleyteaapi.api.abstracts.BaseEntityFeatureData;

public final class DataEntityLostTarget extends BaseEntityFeatureData<EntityTargetEvent> {
    public DataEntityLostTarget(@Nonnull EntityTargetEvent event) {
        super(event);
    }

    @SuppressWarnings("null")
    @Nonnull
    public EntityTargetEvent.TargetReason getReason() {
        return event.getReason();
    }
}
