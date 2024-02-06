package org.ricetea.barleyteaapi.api.base.data;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.ricetea.barleyteaapi.api.entity.CustomEntityType;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;

public abstract class BaseEntityShotFeatureData extends BaseEntityFeatureData<ProjectileHitEvent> {
    @Nonnull
    private final Lazy<CustomEntityType> projectileType;

    public BaseEntityShotFeatureData(@Nonnull ProjectileHitEvent event) {
        super(event, (Entity) event.getEntity().getShooter());
        projectileType = Lazy.create(() -> CustomEntityType.get(getProjectile()));
    }

    @Nonnull
    public Projectile getProjectile() {
        return event.getEntity();
    }

    @Nonnull
    public CustomEntityType getProjectileType() {
        return projectileType.get();
    }
}
