package org.ricetea.barleyteaapi.api.base.data;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityEvent;
import org.ricetea.barleyteaapi.api.entity.CustomEntityType;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public abstract class BaseProjectileFeatureData<T extends EntityEvent> extends BaseFeatureData<T> {
    @Nullable
    private final Entity shooter;

    @Nullable
    private final Lazy<CustomEntityType> shooterType;

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
                shooter -> Lazy.create(() -> CustomEntityType.get(shooter)));
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
    public CustomEntityType getShooterEntityType() {
        return ObjectUtil.safeMap(shooterType, Lazy::get);
    }
}
