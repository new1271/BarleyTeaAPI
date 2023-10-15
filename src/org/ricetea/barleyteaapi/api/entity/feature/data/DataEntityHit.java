package org.ricetea.barleyteaapi.api.entity.feature.data;

import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.ricetea.barleyteaapi.api.abstracts.BaseEntityFeatureData;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

public final class DataEntityHit extends BaseEntityFeatureData<ProjectileHitEvent> {
    @Nullable
    private final Entity shooter;

    @Nonnull
    private final Lazy<DataEntityType> projectileType;

    @Nullable
    private final Lazy<DataEntityType> shooterType;

    @SuppressWarnings("null")
    public DataEntityHit(@Nonnull ProjectileHitEvent event) {
        super(event, event.getHitEntity());
        projectileType = Lazy.create(() -> BaseEntity.getEntityType(getProjectile()));
        shooter = ObjectUtil.tryCast(event.getEntity().getShooter(), Entity.class);
        shooterType = ObjectUtil.mapWhenNonnull(shooter,
                (Function<Entity, Lazy<DataEntityType>>) shooter -> Lazy.create(
                        () -> BaseEntity.getEntityType(shooter)));
    }

    @SuppressWarnings("null")
    @Nonnull
    public Projectile getProjectile() {
        return event.getEntity();
    }

    @Nonnull
    public DataEntityType getProjectileType() {
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
    public DataEntityType getShooterEntityType() {
        Lazy<DataEntityType> shooterType = this.shooterType;
        if (shooterType == null)
            return null;
        return shooterType.get();
    }
}
