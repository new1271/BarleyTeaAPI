package org.ricetea.barleyteaapi.api.base.data;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;

public abstract class BaseEntityShotFeatureData extends BaseEntityFeatureData<ProjectileHitEvent> {
    @Nonnull
    private final Lazy<DataEntityType> projectileType;

    public BaseEntityShotFeatureData(@Nonnull ProjectileHitEvent event) {
        super(event, (Entity) event.getEntity().getShooter());
        projectileType = Lazy.create(() -> BaseEntity.getEntityType(getProjectile()));
    }

    @Nonnull
    public Projectile getProjectile() {
        return event.getEntity();
    }

    @Nonnull
    public DataEntityType getProjectileType() {
        return projectileType.get();
    }
}
