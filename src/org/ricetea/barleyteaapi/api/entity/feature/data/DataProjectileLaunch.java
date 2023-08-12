package org.ricetea.barleyteaapi.api.entity.feature.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.ricetea.barleyteaapi.api.abstracts.DataEntityBase;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.barleyteaapi.util.Lazy;
import org.ricetea.barleyteaapi.util.ObjectUtil;

public final class DataProjectileLaunch extends DataEntityBase<ProjectileLaunchEvent> {
    @Nullable
    private final Entity shooter;

    @Nonnull
    private final Lazy<DataEntityType> entityType;

    @Nullable
    private final Lazy<DataEntityType> shooterType;

    @SuppressWarnings("null")
    public DataProjectileLaunch(@Nonnull ProjectileLaunchEvent event) {
        super(event);
        entityType = new Lazy<>(() -> BaseEntity.getEntityType(event.getEntity()));
        shooter = ObjectUtil.tryCast(event.getEntity().getShooter(), Entity.class);
        shooterType = ObjectUtil.mapWhenNonnull(shooter,
                shooter -> new Lazy<>(() -> BaseEntity.getEntityType(shooter)));
    }

    @SuppressWarnings("null")
    @Nonnull
    public Projectile getEntity() {
        return event.getEntity();
    }

    @Nonnull
    public DataEntityType getEntityType() {
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
}
