package org.ricetea.barleyteaapi.api.entity.feature.data;

import java.util.Objects;

import javax.annotation.Nonnull;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.ricetea.barleyteaapi.api.abstracts.BaseProjectileFeatureData;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.utils.Lazy;

public final class DataProjectileHitEntity extends BaseProjectileFeatureData<ProjectileHitEvent> {
    @Nonnull
    private final Lazy<DataEntityType> hitEntityType;

    public DataProjectileHitEntity(@Nonnull ProjectileHitEvent event) {
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
