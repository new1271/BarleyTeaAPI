package org.ricetea.barleyteaapi.api.entity.feature.data;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.ricetea.barleyteaapi.api.base.data.BaseProjectileFeatureData;
import org.ricetea.barleyteaapi.api.entity.CustomEntityType;
import org.ricetea.utils.Lazy;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class DataProjectileHitEntity extends BaseProjectileFeatureData<ProjectileHitEvent> {
    @Nonnull
    private final Lazy<CustomEntityType> hitEntityType;

    public DataProjectileHitEntity(@Nonnull ProjectileHitEvent event) {
        super(event);
        hitEntityType = Lazy.create(() -> CustomEntityType.get(getHitEntity()));
    }

    @Nonnull
    public Entity getHitEntity() {
        return Objects.requireNonNull(event.getHitEntity());
    }

    @Nonnull
    public CustomEntityType getHitEntityType() {
        return hitEntityType.get();
    }
}
