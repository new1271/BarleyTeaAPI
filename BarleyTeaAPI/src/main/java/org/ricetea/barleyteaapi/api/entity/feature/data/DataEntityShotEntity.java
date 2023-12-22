package org.ricetea.barleyteaapi.api.entity.feature.data;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.ricetea.barleyteaapi.api.abstracts.BaseEntityShotFeatureData;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class DataEntityShotEntity extends BaseEntityShotFeatureData {
    @Nonnull
    private final Lazy<DataEntityType> hitEntityType;

    public DataEntityShotEntity(@Nonnull ProjectileHitEvent event) {
        super(event);
        hitEntityType = Lazy.create(() -> BaseEntity.getEntityType(getHitEntity()));
    }

    @Nonnull
    public Entity getHitEntity() {
        return Objects.requireNonNull(event.getHitEntity());
    }

    @Nonnull
    public DataEntityType getHitEntityType() {
        return hitEntityType.get();
    }
}
