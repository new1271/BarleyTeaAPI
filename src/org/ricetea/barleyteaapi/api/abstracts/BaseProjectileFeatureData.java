package org.ricetea.barleyteaapi.api.abstracts;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityEvent;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.barleyteaapi.util.Lazy;
import org.ricetea.barleyteaapi.util.ObjectUtil;

public abstract class BaseProjectileFeatureData<T extends EntityEvent> extends BaseFeatureData<T> {
    @Nullable
    private final Entity shooter;

    @Nullable
    private final Lazy<DataEntityType> shooterType;

    @Nonnull
    private final Projectile entity;

    public BaseProjectileFeatureData(@Nonnull T event) {
        this(event, (Projectile) event.getEntity());
    }

    @SuppressWarnings("null")
    public BaseProjectileFeatureData(@Nonnull T event, @CheckForNull Projectile entity) {
        super(event);
        this.entity = ObjectUtil.throwWhenNull(entity);
        shooter = ObjectUtil.tryCast(entity.getShooter(), Entity.class);
        shooterType = ObjectUtil.mapWhenNonnull(shooter,
                shooter -> new Lazy<>(() -> BaseEntity.getEntityType(shooter)));
    }

    @Nonnull
    public Projectile getEntity() {
        return entity;
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