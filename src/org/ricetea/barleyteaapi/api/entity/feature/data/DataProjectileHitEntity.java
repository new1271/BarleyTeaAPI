package org.ricetea.barleyteaapi.api.entity.feature.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.ricetea.barleyteaapi.api.abstracts.DataEntityBase;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.barleyteaapi.util.Lazy;

public final class DataProjectileHitEntity extends DataEntityBase<ProjectileHitEvent> {
    @Nullable
    private final Entity shooter;

    @Nonnull
    private final Lazy<DataEntityType> entityType, hitEntityType;

    @Nullable
    private final Lazy<DataEntityType> shooterType;

    @SuppressWarnings("null")
    public DataProjectileHitEntity(@Nonnull ProjectileHitEvent event) {
        super(event);
        entityType = new Lazy<>(() -> BaseEntity.getEntityType(event.getEntity()));
        hitEntityType = new Lazy<>(() -> BaseEntity.getEntityType(event.getHitEntity()));
        ProjectileSource shooter = event.getEntity().getShooter();
        if (shooter instanceof Entity) {
            Entity shooterEntity = (Entity) shooter;
            this.shooter = shooterEntity;
            shooterType = new Lazy<>(() -> BaseEntity.getEntityType(this.shooter));
        } else {
            this.shooter = null;
            shooterType = null;
        }
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