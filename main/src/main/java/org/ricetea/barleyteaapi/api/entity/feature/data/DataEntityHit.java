package org.ricetea.barleyteaapi.api.entity.feature.data;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.ricetea.barleyteaapi.api.base.data.BaseEntityFeatureData;
import org.ricetea.barleyteaapi.api.entity.CustomEntityType;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class DataEntityHit extends BaseEntityFeatureData<ProjectileHitEvent> {
    @Nullable
    private final Entity shooter;

    @Nonnull
    private final Lazy<CustomEntityType> projectileType;

    @Nullable
    private final Lazy<CustomEntityType> shooterType;

    public DataEntityHit(@Nonnull ProjectileHitEvent event) {
        super(event, event.getHitEntity());
        projectileType = Lazy.create(() -> CustomEntityType.get(getProjectile()));
        shooter = ObjectUtil.tryCast(event.getEntity().getShooter(), Entity.class);
        shooterType = ObjectUtil.safeMap(shooter,
                shooter -> Lazy.create(
                        () -> CustomEntityType.get(shooter)));
    }

    @Nonnull
    public Projectile getProjectile() {
        return event.getEntity();
    }

    @Nonnull
    public CustomEntityType getProjectileType() {
        return projectileType.get();
    }

    public boolean hasShooterEntity() {
        return shooter != null;
    }

    @Nullable
    public Entity getShooterEntity() {
        return shooter;
    }

    @Nullable
    public CustomEntityType getShooterEntityType() {
        Lazy<CustomEntityType> shooterType = this.shooterType;
        if (shooterType == null)
            return null;
        return shooterType.get();
    }
}
