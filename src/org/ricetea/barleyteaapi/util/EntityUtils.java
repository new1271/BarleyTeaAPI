package org.ricetea.barleyteaapi.util;

import javax.annotation.Nullable;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.ProjectileSource;

public final class EntityUtils {

    @Nullable
    public static Entity getProjectileShooterEntity(Projectile projectile) {
        ProjectileSource source = projectile.getShooter();
        if (source instanceof Entity) {
            return (Entity) source;
        } else {
            return null;
        }
    }
}
