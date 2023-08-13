package org.ricetea.barleyteaapi.api.entity.feature.data;

import javax.annotation.Nonnull;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.ricetea.barleyteaapi.api.abstracts.BaseEntityShotFeatureData;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.barleyteaapi.util.Lazy;

public final class DataEntityShotEntity extends BaseEntityShotFeatureData {
    @Nonnull
    private final Lazy<DataEntityType> hitEntityType;

    public DataEntityShotEntity(@Nonnull ProjectileHitEvent event) {
        super(event);
        hitEntityType = new Lazy<>(() -> BaseEntity.getEntityType(getHitEntity()));
    }

    @SuppressWarnings("null")
    @Nonnull
    public Entity getHitEntity() {
        return event.getHitEntity();
    }

    @Nonnull
    public DataEntityType getHitEntityType() {
        return hitEntityType.get();
    }
}
