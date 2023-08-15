package org.ricetea.barleyteaapi.api.entity.feature.data;

import javax.annotation.Nonnull;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.ricetea.barleyteaapi.api.abstracts.BaseEntityFeatureData;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.barleyteaapi.util.Lazy;

public final class DataEntityShoot extends BaseEntityFeatureData<ProjectileLaunchEvent> {
    @Nonnull
    private final Lazy<DataEntityType> entityType;

    public DataEntityShoot(@Nonnull ProjectileLaunchEvent event) {
        super(event, (Entity) event.getEntity().getShooter());
        entityType = new Lazy<>(() -> BaseEntity.getEntityType(getProjectile()));
    }

    @SuppressWarnings("null")
    @Nonnull
    public Projectile getProjectile() {
        return event.getEntity();
    }

    @Nonnull
    public DataEntityType getProjectileType() {
        return entityType.get();
    }
}
