package org.ricetea.barleyteaapi.api.entity.feature.data;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.ricetea.barleyteaapi.api.base.data.BaseEntityFeatureData;
import org.ricetea.barleyteaapi.api.entity.CustomEntityType;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;

public final class DataEntityShoot extends BaseEntityFeatureData<ProjectileLaunchEvent> {
    @Nonnull
    private final Lazy<CustomEntityType> entityType;

    public DataEntityShoot(@Nonnull ProjectileLaunchEvent event) {
        super(event, (Entity) event.getEntity().getShooter());
        entityType = Lazy.create(() -> CustomEntityType.get(getProjectile()));
    }

    @Nonnull
    public Projectile getProjectile() {
        return event.getEntity();
    }

    @Nonnull
    public CustomEntityType getProjectileType() {
        return entityType.get();
    }
}
