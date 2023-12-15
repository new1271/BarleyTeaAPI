package org.ricetea.barleyteaapi.api.abstracts;

import java.util.Objects;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityEvent;
import org.ricetea.barleyteaapi.api.entity.BaseEntity;
import org.ricetea.barleyteaapi.api.entity.data.DataEntityType;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

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

    public BaseProjectileFeatureData(@Nonnull T event, @CheckForNull Projectile entity) {
        super(event);
        this.entity = Objects.requireNonNull(entity);
        shooter = ObjectUtil.tryCast(entity.getShooter(), Entity.class);
        shooterType = ObjectUtil.safeMap(shooter,
                shooter -> Lazy.create(() -> BaseEntity.getEntityType(shooter)));
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
