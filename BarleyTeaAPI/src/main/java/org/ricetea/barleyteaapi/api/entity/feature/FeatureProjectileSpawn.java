package org.ricetea.barleyteaapi.api.entity.feature;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.ProjectileSource;

public interface FeatureProjectileSpawn extends FeatureEntitySpawn {
    @Nullable
    Projectile handleEntitySpawn(@Nonnull Location location);

    @Nullable
    Projectile handleEntitySpawn(@Nonnull Location location, @Nullable ProjectileSource source);
}
