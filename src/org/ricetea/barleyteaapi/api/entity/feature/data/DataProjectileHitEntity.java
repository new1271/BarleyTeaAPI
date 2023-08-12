package org.ricetea.barleyteaapi.api.entity.feature.data;

import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.ricetea.barleyteaapi.api.abstracts.DataEntityBase;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.barleyteaapi.util.Lazy;
import org.ricetea.barleyteaapi.util.ObjectUtil;

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
        shooter = ObjectUtil.tryCast(event.getEntity().getShooter(), Entity.class);
        shooterType = ObjectUtil.callWhenNonnull(shooter,
                (Function<Entity, Lazy<DataEntityType>>) shooter -> new Lazy<>(
                        () -> BaseEntity.getEntityType(shooter)));
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
